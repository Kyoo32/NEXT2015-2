#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <dirent.h>
#include <fcntl.h>
#include <time.h>
#include <errno.h>
#include <pthread.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/wait.h>


/* 상수 정의 */
#define MAXLINE		1024
#define MAXARGS		128
#define MAXPATH		1024
#define MAXTHREAD	10

#define MAXBUFF     1024

#define DEFAULT_FILE_MODE	0664
#define DEFAULT_DIR_MODE	0775


/* 전역 변수 정의 */
char prompt[] = "myshell> ";
const char delim[] = " \t\n";
static char perms_buff[30];



/* 함수 선언 */
void myshell_error(char *err_msg);
void process_cmd(char *cmdline);
int parse_line(char *cmdline, char **argv);
int builtin_cmd(int argc, char **argv);


// 내장 명령어 처리 함수
int list_files(int argc, char **argv);
int copy_file(int argc, char **argv);
int inner_file_copy(const char* src, const char* dst);
int remove_file(int argc, char **argv);
int move_file(int argc, char **argv);
int change_directory(int argc, char **argv);
int make_directory(int argc, char **argv);
int remove_directory(int argc, char **argv);
int copy_directory(int argc, char **argv);
int concatenate(int argc, char** argv);
int print_working_directory(void);

//파일 사용자 권한 문자열처리 함수
const char* my_sperms(mode_t mode);



/*
 * main - MyShell's main routine
 */
int main()
{
	char cmdline[MAXLINE];

	/* 명령어 처리 루프: 셸 명령어를 읽고 처리한다. */
	while (1) {
		// 프롬프트 출력
		printf("%s", prompt);
		fflush(stdout);

		// 명령 라인 읽기
		if (fgets(cmdline, MAXLINE, stdin) == NULL) {
			return 1;
		}

		// 명령 라인 처리
		process_cmd(cmdline);

		fflush(stdout);
	}

	/* 프로그램이 도달하지 못한다. */
	return 0;
}



/*
 * process_cmd
 *
 * 명령 라인을 인자 (argument) 배열로 변환한다.
 * 내장 명령 처리 함수를 수행한다.
 * 내장 명령이 아니면 자식 프로세스를 생성하여 지정된 프로그램을 실행한다.
 * 파이프(|)를 사용하는 경우에는 두 개의 자식 프로세스를 생성한다.
 */
void process_cmd(char *cmdline)
{
	int argc;
	char *argv[MAXARGS];
   
	// 명령 라인을 해석하여 인자 (argument) 배열로 변환한다.
	argc = parse_line(cmdline, argv);
    if (argc == 0) return ;
    
    /*
    // 명령 확인용
	int i=0;
	for(i=0; i <argc; i++){
       
		printf("@%s ",argv[i]);
	}
	printf("\n");
    */
    
    //리다이렉션 여부 확인하여 스트림 바꿔주기
    int fd, saved_stdout = 1;
    if( (argc>1 && !strcmp(argv[1], ">")) || ((argc>2) && !strcmp(argv[2], ">")) ) {
        
        //확인용
        //printf("redirection\n");
        fd = creat(argv[2], DEFAULT_FILE_MODE);
        if(fd < 0) {
            perror("file open error\n");
            return ;
        }
        saved_stdout = dup(1);
        dup2(fd, 1);
        close(fd);
    }
    


	/* 내장 명령 처리 함수를 수행한다. */
	if (builtin_cmd(argc, argv) == 0) {

        if((argc>1 && !strcmp(argv[1], ">")) || ((argc>2) && !strcmp(argv[2], ">"))){
            //리다이렉션이 있을 경우
            //표준출력스트림 재정의하기
            dup2(saved_stdout, 1);
            
            
        }
		// 내장 명령 처리를 완료하고 리턴한다.
		return;
	}
    

	/*
	 * 자식 프로세스를 생성하여 프로그램을 실행한다.
	 */
    
    //  파이프 명령어 있을 경우 사용.
    pid_t pid, pid2;
    int pipefd[2];
    
    int hasPipe = 0;
    int i=0;
    
    // 파이프 명령어 존재 확인
    for(i=0; i< argc; i++){
        if(!strcmp(argv[i], "|")){
            hasPipe = 1;
           // printf("pipe = %d \n", hasPipe); //파이프 존재 확인 용
            break;
        }
    }
    
	
    // 파이프 명령어 있을 떼, 파이프 생성
    if(hasPipe){
        if(pipe(pipefd) == -1) return ;
    }
    
    // (첫번째 자식) 프로세스 생성
    pid = fork();
    if(pid < 0){
        //fork error
        fprintf(stderr, "1Fork failed");
        return;

    }
    else if(pid ==0){ //(첫번째 자식) 프로세서
        
        if(*argv[argc-1] == '&') { // 백그라운드 모드 일때
            /*
            printf("[bg] %d : %s \n", getpid(), argv[0]);
            int k = 0;
            for(k=0;k<10;k++){
                printf("background job...(%d)\n", k+1);
                sleep(1);
                
            }
             */
             pid_t pid_bg = fork();
            
            if(pid_bg < 0){
                //fork error
                fprintf(stderr, "1Fork failed");
                exit(-1);
                return;

            }
            else if(pid_bg == 0){
                printf("[bg] %d : %s \n", getppid(), argv[0]);
                
                char temp[20] = "which ";
                strcat(temp, argv[0]);
                
                int ret = system(temp); //if exec has error, exit(-1);
                //printf("ret = %d", ret); //for debugging
                
                if(ret != 0) exit(-1);
                
                if(execvp(argv[0], argv) < 0 ) {
                    fprintf(stderr, "exec error \n");
                    exit(-1);
                    return;
                };
            }
            
            return ;
        }
        else {
            
            if(hasPipe){ // 파이프 명령어 있을 때
             
                close(pipefd[0]);

                if( dup2(pipefd[1], STDOUT_FILENO) < 0 ) {
                    fprintf(stderr, "pipefd duplicatiion error\n");
                    return;
                };
                // 파이프 이전 명령어, 임시 저장
                int j=0;
                char* temp[MAXARGS];
                for(j=0; j<i; j++){
                    temp[j] = argv[j];
                }
                temp[j] = NULL;
                if(execvp(temp[0], temp) < 0 ) {
                    fprintf(stderr, "exec error : 1\n");
                    return;
                };
                fprintf(stderr, "I can't be here\n");
                
            }
            
            else{ //파이프 명령어 없을 때
                execvp(argv[0], argv);
            }
        }
        
    }
	// 파이프 실행이면 자식 프로세스를 하나 더 생성하여 파이프로 연결
    if(hasPipe){
        pid2 = fork();
    }
    if(pid2<0){
        // fork error
        fprintf(stderr, "Fork failed");
        return;
    }
    if(pid2 == 0){
        close(pipefd[1]);
        
        if(dup2(pipefd[0], STDIN_FILENO) <0 ){
            fprintf(stderr, "pipefd duplication error\n");
            return;
        };
        
        //파이프 이후 명령어 임시 배열 저장
        int j=0;
        char* temp[MAXARGS];
        for(j=i+1; j<argc; j++){
            temp[j-(i+1)] = argv[j];
        }
        temp[j-(i+1)] = NULL;
        if(execvp(temp[0], temp) < 0 ) {
            fprintf(stderr, "exec error : 2\n");
            return;
        };
    }
	
    if(hasPipe){ //파이프 실행 시 부모 프로세스가 파이프를 닫음
        close(pipefd[0]);
        close(pipefd[1]);
        waitpid(pid2, NULL, 0);
    }
        
    if( *argv[argc-1] == '&'){ // foreground 실행이면 자식 프로세스가 종료할 때까지 기다린다.
        while( waitpid(-1, NULL, WNOHANG)!=0 );
    }
    else{
        waitpid(pid,NULL,0);
    }
    
    //리다이렉션 시, 표준출력스트림 재정의하기
    if((argc>1 && !strcmp(argv[1], ">")) || ((argc>2) && !strcmp(argv[2], ">"))){
        dup2(saved_stdout, 1);
    }
	return;
}


/*
 * parse_line
 *
 * 명령 라인을 인자(argument) 배열로 변환한다.
 * 인자의 개수(argc)를 리턴한다.
 * 파이프와 백그라운드 실행, 리다이렉션을 해석하고 flag와 관련 변수를 
 *   설정한다.
 */
int parse_line(char *cmdline, char **argv)
{
	// delimiter 문자를 이용하여 cmdline 문자열 분석
    int i=0;
    int argc = 0;
    
    char *token = strtok(cmdline, delim);
    while(token){
        argv[i] = token;
        i++ ;
        token = strtok(NULL, delim);
        
    }
    argv[i] = NULL;
    argc = i;
    
    return argc;
}



/*
 * builtin_cmd
 *
 * 내장 명령을 수행한다.
 * 내장 명령이 아니면 1을 리턴한다.
 */
int builtin_cmd(int argc, char **argv)
{
    /*
    //리다이렉션 여부 확인하여 스트림 바꿔주기
    int fd, saved_stdout = 1;
    if(argc>1 && !strcmp(argv[1], ">")){
    
        
        fd = creat(argv[2], DEFAULT_FILE_MODE);
        if(fd < 0) {
            perror("file open error\n");
            return -1;
        }
        saved_stdout = dup(1);
        dup2(fd, 1);
        close(fd);
    }
     */
    
    // 내장 명령어 문자열과 argv[0]을 비교하여 각각의 처리 함수 호출
	if ( (!strcmp(argv[0], "quit") || (!strcmp(argv[0], "exit")))){
       		exit(0);
	}
    else if(!strcmp(argv[0], "ls") || !strcmp(argv[0], "ll")){
        list_files(argc, argv);
    }
    else if(!strcmp(argv[0],"cp")){
        copy_file(argc,  argv);
    }
    else if(!strcmp(argv[0],"mv")){
        move_file(argc,  argv);
    }
    else if(!strcmp(argv[0], "rm")){
        remove_file(argc, argv);
    }
    else if (!strcmp(argv[0], "pwd")){
        print_working_directory();
    }
    else if(!strcmp(argv[0], "mkdir")){
        make_directory(argc,  argv);
    }
    else if(!strcmp(argv[0], "rmdir")){
        remove_directory(argc, argv);
    }
    else if(!strcmp(argv[0], "cd")){
        change_directory(argc,  argv);
    }
    else if(!strcmp(argv[0], "cat")){
        int nResult = concatenate(argc, argv);
        if( nResult == -1) printf("concatenate error\n");
    }
    else{
        //printf("no such command\n");
        return 1;
    }// 내장 명령어가 아님.
    
    /*
    //리다이렉션 시, 표준출력스트림 재정의하기
    if(argc>1 && !strcmp(argv[1], ">")){
        dup2(saved_stdout, 1);
    }
     */
    
	return 0;
}


/* 내장 명령 처리 함수들 :argc, argv를 인자로 받는다.*/


/*
 * list_files
 *
 * ls, ll, ls >, ll > 처리
 *
 */
int list_files(int argc, char **argv)
{
   
    DIR *dp;
    struct dirent *d_entry;
    struct stat sb;
    int ret;
    char buff[40];
    time_t t = (time_t)NULL;
    struct tm lt;
    
    // 인자가 3개 초과면 에러처리
    if(argc > 3) {
        perror("too many arguments\n");
        return -1;
    }
   
    
    dp = opendir(".");
    if (dp == NULL){
        perror("directory open errror\n");
        return -1;
    }
    
    d_entry = readdir(dp);
    if( !strcmp(argv[0], "ls" )){
        while(d_entry != NULL){
            printf("%s\n", d_entry->d_name);
            d_entry = readdir(dp);
        }
    }
    else if( !strcmp(argv[0], "ll") ){
        while( d_entry != NULL){
            ret =stat(d_entry->d_name, &sb);
            if(ret){
                perror("read stat error\n");
            return -1;
            }
            
            t = sb.st_atime;
            localtime_r(&t, &lt);
            strftime(buff, sizeof buff, "%b %d %T", &lt);
            
            printf("%10.10s %2hu %5u %3u %8ld %s %s \n", my_sperms(sb.st_mode), sb.st_nlink, sb.st_uid, sb.st_gid, sb.st_size, buff,  d_entry->d_name);
            d_entry = readdir(dp);
        }
    }
    closedir(dp);
    
	return 0;
}

/*
 * copy_file
 *
 * cp [src file] [dst file]
 *
 */
int copy_file(int argc, char **argv)
{
    
    // 인자가 3개 미만면 에러처리
    if(argc < 3) {
        perror("lack argument\n");
        return -1;
    }
    
    int nResult = inner_file_copy(argv[1], argv[2]);
    
    if(nResult == -1){
        perror("file copy error\n");
    }
	return 0;
}

/*
 * move_file
 *
 * mv [src file] [dst file]
 *
 */
int move_file(int argc, char **argv){
    
    // 인자가 3개 미만이면 에러처리
    if(argc < 3) {
        perror("lack argument\n");
        return -1;
    }
    
    int nResult = rename( argv[1], argv[2] );
    
    if( nResult == -1 )
    {
        perror( "move file error\n" );
    }
    
    return 0;
}

/*
 * remove_file
 *
 * rm [file]
 *
 */
int remove_file(int argc, char **argv)
{
    // 인자가 2개 미만이면 에러처리
    if(argc < 2) {
        perror("lack argument\n");
        return -1;
    }
    int nResult = remove(argv[1] );
    
    if( nResult == -1 )
    {
        perror( "remove file error\n" );
    }

	return 0;
}

/*
 * change_directory
 *
 * cd [dst directory]
 *
 */
int change_directory(int argc, char **argv)
{
    // 인자가 2개 미만이면 에러처리
    if(argc < 2) {
        perror("lack argument\n");
        return -1;
    }
    int nResult = chdir(argv[1]);
    if( nResult == -1 ){
        printf("no such directory\n");
    };
	return 0;
}

/*
 * print_working_directory
 *
 * show working directory
 * pwd
 */
int print_working_directory(void)
{
    char buff[MAXBUFF];
    getcwd(buff,MAXBUFF);
    printf("%s \n", buff);
	return 0;
}

/*
 * make_directory
 * 
 * mkdir [directory name]
 *
 */
int make_directory(int argc, char **argv)
{
    // 인자가 2개 미만이면 에러처리
    if(argc < 2) {
        perror("lack argument\n");
        return -1;
    }
    
    int nResult = mkdir(argv[1], DEFAULT_DIR_MODE );
    
    if( nResult == -1 )
    {
        perror( "such directory already exists or wrong name\n" );
    }

	return 0;
}

/*
 * remove_directory
 *
 * rmdir [directory name]
 *
 */
int remove_directory(int argc, char **argv)
{
    // 인자가 2개 미만이면 에러처리
    if(argc < 2) {
        perror("lack argument\n");
        return -1;
    }
    int nResult = remove(argv[1]);
   
    if( nResult == -1 )
    {
        perror( "other folder exists in it or used now\n" );
    }

	return 0;
}

/*
 * concatenate
 * show the file
 * cat [file name]
 */
int concatenate(int argc, char** argv){
    
    // 인자가 2개 미만이면 에러처리
    if(argc < 2) {
        perror("lack argument\n");
        return -1;
    }
    
    int in_fd, rd_count, standard_out = 1;
    char buffer[MAXBUFF];
    
    if(argc > 2) return -1;
    
    if( (in_fd = open(argv[1] , O_RDONLY))< 0)
        return -1;
    
    while(1){
        if(  (rd_count = read(in_fd, buffer, 1024) ) <= 0  )
            break;
        if( (write(standard_out, buffer, rd_count) <= 0 ))
            return -1;
    }
    
    
    if( rd_count < 0 )  return -1;
    close(in_fd);
    
    
    return 0;
}

/*
 * copy_directory
 * dcp [src] [new name]
 *
 */
int copy_directory(int argc, char **argv)
{
    
	return 0;
}

//file copy 수행 함수
int inner_file_copy(const char* src, const char* dst){
    FILE *in, *out;
    char* buf;
    size_t len;
    

    if ((in  = fopen(src, "rb")) == NULL) return -1; // 원본 파일 열기
    if ((out = fopen(dst, "wb")) == NULL) { fclose(in); return -1; } // 대상 파일 만들기
    
    if ((buf = (char *) malloc(MAXBUFF)) == NULL) { fclose(in); fclose(out); return 10; } // 버퍼 메모리 할당
    
    while ( (len = fread(buf, sizeof(char), sizeof(buf), in)) != (int)NULL )
        if (fwrite(buf, sizeof(char), len, out) == 0) {
            fclose(in); fclose(out);
            free(buf);
            unlink(dst); // 에러난 파일 지우고 종료
            return -1;
        }
    
    fclose(in); fclose(out);
    free(buf); // 메모리 할당 해제
    
    return 0;
}

//파일 사용자 권한 문자열처리 함수
const char* my_sperms(mode_t mode)
{
    char ftype = '?';
    if (S_ISREG(mode)) ftype = '-';
    if (S_ISLNK(mode)) ftype = 'l';
    if (S_ISDIR(mode)) ftype = 'd';
    if (S_ISBLK(mode)) ftype = 'b';
    if (S_ISCHR(mode)) ftype = 'c';
    if (S_ISFIFO(mode)) ftype = '|';
    //if (S_ISINDEX(mode)) ftype = 'i';
    sprintf(perms_buff, "%c%c%c%c%c%c%c%c%c%c %c%c%c",
            ftype,
            mode & S_IRUSR ? 'r' : '-',
            mode & S_IWUSR ? 'w' : '-',
            mode & S_IXUSR ? 'x' : '-',
            mode & S_IRGRP ? 'r' : '-',
            mode & S_IWGRP ? 'w' : '-',
            mode & S_IXGRP ? 'x' : '-',
            mode & S_IROTH ? 'r' : '-',
            mode & S_IWOTH ? 'w' : '-',
            mode & S_IXOTH ? 'x' : '-',
            mode & S_ISUID ? 'U' : '-', 
            mode & S_ISGID ? 'G' : '-', 
            mode & S_ISVTX ? 'S' : '-'); 
    return (const char *)perms_buff; 
}