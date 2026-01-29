#include "dbg.h"
#include <glob.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

struct Connection {
  FILE *file;
};

struct Connection *file_open(char *filename)
{
  struct Connection *conn = malloc(sizeof(struct Connection));
  conn->file = fopen(filename, "r");
  check(conn->file != NULL, "Failed to open file.");

  return conn;

error:
  free(conn);
  exit(1);
}

int file_scan(struct Connection *conn, char *pattern)
{
 char buf[512] = "";
 rewind(conn->file);

 while(!feof(conn->file)) {
   fgets(buf, sizeof(buf), conn->file);
   buf[sizeof(buf) - 1] = '\0';
   for(int i = 0; i < sizeof(buf) || buf[i] != '\0'; i++) {
      int rc = strncmp(buf + i, pattern, strlen(pattern));
      if (rc == 0) return 1;
   }
 }

  return 0;
}

void  or_search( int argc, char *argv[]) {

  glob_t gstruct;
  int rc = glob("*.*", GLOB_ERR, NULL, &gstruct);

  for(int i = 0; i < gstruct.gl_pathc; i++) {
    struct Connection *conn = file_open(gstruct.gl_pathv[i]);
    for(int j = 2; j < argc; j++) {
      rc = file_scan(conn, argv[j]);
      if (rc == 1) {
        printf("%s\n", gstruct.gl_pathv[i]);
        break;
      }
    }
    free(conn);
  }
}

void and_search( int argc, char *argv[]) {

  glob_t gstruct;
  int rc = glob("*.*", GLOB_ERR, NULL, &gstruct);

  for(int i = 0; i < gstruct.gl_pathc; i++) {
    struct Connection *conn = file_open(gstruct.gl_pathv[i]);
    for(int j = 1; j < argc; j++) {
      rc = file_scan(conn, argv[j]);
      if (rc == 1 && j == argc - 1) printf("%s\n", gstruct.gl_pathv[i]);
      else if (rc == 1) continue;
      else break;
    }
    free(conn);
  }
}

int main(int argc, char *argv[]) {
  check(argc >= 2, "Invalid input: ./logfind <OPTIONS> <PATTERN>")

  if(argv[1][0] == '-') {
    switch (argv[1][1]) {
      case 'o':
        or_search( argc, argv);
        break;

      default:
        printf("Invalid input: -o required\n");
        exit(1);
        break;
    }
  }
  else {
    and_search(argc, argv);
  }
  return 0;

error:
  return 1;
}
