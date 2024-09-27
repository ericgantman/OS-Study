#include <sys/types.h>
#include <sys/stat.h>

#include <fcntl.h>
#include <getopt.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#define MAX_BUFFER_SIZE 65536
#define DESTINATION_FILE_MODE S_IRUSR | S_IWUSR | S_IRGRP | S_IROTH

extern int opterr, optind;

void exit_with_usage(const char *message)
{
	fprintf(stderr, "%s\n", message);
	fprintf(stderr, "Usage:\n\tex1 [-f] BUFFER_SIZE SOURCE DEST\n");
	exit(EXIT_FAILURE);
}

void copy_file(const char *source_file, const char *dest_file, int buffer_size, int force_flag)
{
	/*
	 * Copy source_file content to dest_file, buffer_size bytes at a time.
	 * If force_flag is true, then also overwrite dest_file. Otherwise print error, and exit.
	 *
	 * TODO:
	 * 	1. Open source_file for reading
	 * 	2. Open dest_file for writing (Hint: is force_flag true?)
	 * 	3. Loop reading from source and writing to the destination buffer_size bytes each time
	 * 	4. Close source_file and dest_file
	 *
	 *  ALWAYS check the return values of syscalls for errors!
	 *  If an error was found, use perror(3) to print it with a message, and then exit(EXIT_FAILURE)
	 */
	// Initialize variables to hold information about the source and destination files
	int source_data, destination_data;
	// Initialize variables to store the number of bytes read and written
	long read_bytes, write_bytes;
	// Initialize a buffer to store data read from source file
	char buffer[MAX_BUFFER_SIZE];
	// Open the source file for reading only
	source_data = open(source_file, O_RDONLY);
	if (source_data == -1) // if an error occurred while opening the source file
	{
		// Print an error message "Unable to open source file for reading"
		perror("Unable to open source file for reading");
		// Exit the program with failure status
		exit(EXIT_FAILURE);
	}
	// Open destination file for writing
	int mode = O_WRONLY | O_CREAT; // Set mode for opening destination file, including write-only and create flags
	if (force_flag)
		mode |= O_TRUNC; // If force_flag is true, overwrite dest_file
	else
		mode |= O_EXCL; // Otherwise, fail if dest_file exists
	// Open the destination file with the specified mode and file permissions
	destination_data = open(dest_file, mode, DESTINATION_FILE_MODE);
	if (destination_data == -1) // if an error occurred while opening the destination file
	{
		// Print an error message "Unable to open destination file for writing"
		perror("Unable to open destination file for writing");
		close(source_data); // close the source_data file
		// Exit the program with failure status
		exit(EXIT_FAILURE);
	}
	// Read data from source file into buffer, up to buffer_size bytes, and store the number of bytes read
	while ((read_bytes = read(source_data, buffer, buffer_size)) > 0)
	{
		// Write the content of the buffer to the destination file
		write_bytes = write(destination_data, buffer, read_bytes);
		// Check if all bytes were successfully written
		if (write_bytes != read_bytes)
		{
			// Print error message "Unable to write buffer content to destination file"
			perror("Unable to write buffer content to destination file");
			close(source_data);		 // closing the source_data file
			close(destination_data); // closing the destination_data file
			// Exit the program with failure status
			exit(EXIT_FAILURE);
		}
	}
	// Check if an error occurred while reading from the source file
	if (read_bytes == -1)
	{
		// Print error message "Unable to read source file"
		perror("Unable to read source file");
		close(source_data);		 // closing the source_data file
		close(destination_data); // closing the destination_data file
		// Exit the program with failure status
		exit(EXIT_FAILURE);
	}
	if (close(source_data) == -1) // checking errors while closing source_data file
	{
		// Print error message "Unable to close source file"
		perror("Unable to close source file");
		close(destination_data); // closing the destination_data file
		// Exit the program with failure status
		exit(EXIT_FAILURE);
	}
	if (close(destination_data) == -1) // checking errors while closing destination_data file
	{
		// Print error message "Unable to close destination file"
		perror("Unable to close destination file");
		// Exit the program with failure status
		exit(EXIT_FAILURE);
	}
	// success message for copying file
	printf("File %s was successfully copied to %s\n", source_file, dest_file);
}

void parse_arguments(
	int argc, char **argv,
	char **source_file, char **dest_file, int *buffer_size, int *force_flag)
{
	/*
	 * parses command line arguments and set the arguments required for copy_file
	 */
	int option_character;

	opterr = 0; /* Prevent getopt() from printing an error message to stderr */

	while ((option_character = getopt(argc, argv, "f")) != -1)
	{
		switch (option_character)
		{
		case 'f':
			*force_flag = 1;
			break;
		default: /* '?' */
			exit_with_usage("Unknown option specified");
		}
	}

	if (argc - optind != 3)
	{
		exit_with_usage("Invalid number of arguments");
	}
	else
	{
		*source_file = argv[argc - 2];
		*dest_file = argv[argc - 1];
		*buffer_size = atoi(argv[argc - 3]);

		if (strlen(*source_file) == 0 || strlen(*dest_file) == 0)
		{
			exit_with_usage("Invalid source / destination file name");
		}
		else if (*buffer_size < 1 || *buffer_size > MAX_BUFFER_SIZE)
		{
			exit_with_usage("Invalid buffer size");
		}
	}
}

int main(int argc, char **argv)
{
	int force_flag = 0; /* force flag default: false */
	char *source_file = NULL;
	char *dest_file = NULL;
	int buffer_size = MAX_BUFFER_SIZE;

	parse_arguments(argc, argv, &source_file, &dest_file, &buffer_size, &force_flag);

	copy_file(source_file, dest_file, buffer_size, force_flag);

	return EXIT_SUCCESS;
}
