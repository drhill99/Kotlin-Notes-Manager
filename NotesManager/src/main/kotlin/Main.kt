import java.io.*
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

    // create new text file
fun createFile(fileName: String, directoryName: String) {
    println("Creating a new file.\n")
    var filePath = "$directoryName/$fileName.txt" // initialize file path
    filePath = filePath.trim() // remove any whitespace
    val file = File(filePath) // create file object from file path
    print("$directoryName/$fileName editing:>> ")
    val userTextInput = readln() // take user input to write text to file
    // try block writes to file. If this initial write is not done, then the file
    // is not created
    try {
        // create writer pointing at the desired file
        val writer = BufferedWriter(FileWriter(file))
        // write to the file
        writer.write(userTextInput)
        // kill the writer object
        writer.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    // jump to looping file append function
    appendToFile(fileName, directoryName)
}

    // take filename, word to find, and file directory arguments, and search for the given word
    // line by line in the specified file
fun findWordInFile(fileName: String, wordToFind: String, directoryName: String) {

        // check that the file exists in the directory
    if(getListOfAllFiles(directoryName, print=false).contains("$fileName.txt")){
        println("finding references to \"$wordToFind\".........")
            // creat list of strings, each being a line from the file.
        val fileAsStrings = readFileAsLines(fileName, directoryName)
        var wordFound = false // boolean holds truth value of the word being found
            // outer loop, steps through each line in the file, each iteration captures the string line
            // and the line index of the active line
        outerLoop@ for ((lineNumber, line) in fileAsStrings.withIndex()) {
                // inner loop, steps through each character in the string line as an index.
            innerLoop@ for (i in line.indices) {
                    // skip the remaining characters in the line if the length of the word is greater
                    // than the number of remaining characters.
                if (i + wordToFind.length > line.length) {
                        // breaks innerloop and continues outerloop
                    continue@outerLoop
                }
                    // create stubstring of the current string line from the current character index through the length
                    // of the word. convert to lowercase for comparison simplification
                val testWord = line.substring(i, i + wordToFind.length).lowercase()
                    // check that the wordToFind.length is smaller than the remaining number of characters
                    // prevents out of bounds indexing as the subsequent if statements check for a space in front and behind
                    // or just in front of the word. This prevents the find function from finding the given word as
                    // a part of a larger word.
                if ((i + wordToFind.length) < line.length) {
                        // checks that the testword is equal to the word to find. 2nd statement checks that the start
                        // index of the testword in the line is either the first index of the line, meaning nothing in
                        // front, or checks that the character in front of the first index of the testword is a space,
                        // then checks that the character after the word is a space.
                    if (testWord == wordToFind && (i == 0 || line[i - 1] == ' ') && line[i + wordToFind.length] == ' ') {
                        wordFound = true // changes wordFound boolean to true
                        println("   Line:${lineNumber + 1}   $line") // print line # and the line the word was found in.
                            // break the inner loop if the word is found early in the line
                        break@innerLoop
                    }
                        // if the remaining letters are equal or less than the size of the word. checks for equality of the
                        // testword to the wordToFind and checks for space in front of the word.
                } else {
                    if (testWord == wordToFind && (i == 0 || line[i - 1] == ' ')) {
                        wordFound = true // changes wordFound boolean to true
                        println("Line: ${lineNumber + 1} $line") // print line # and the line word was found in
                            // break the inner loop if the word is found early in the line
                        break@innerLoop
                    }
                }
            }
        }
        // if false, print to user that the word was not found in the given file
        if(!wordFound){
            println("No references to \"$wordToFind\" found.")
        }
        // if file not found in the directory, print to user
    } else {
        println("invalid input: file does not exist")
        return
    }
}

    // similar to findWordInFile, takes a wordToFind string, and a directoryName. Then steps through each file in the
    // directory calling the findWordInFile function.
fun findWordInDirectory(wordToFind: String, directoryName: String){
        // create a string list of files in the given directory
    val listOfFilesInDir = getListOfAllFiles(directoryName, print = false)
        // for each file in the list of file names
    for(file in listOfFilesInDir){
            // find index of . to pull off .txt extension
        val fileExtensionIndex = file.indexOf('.')
            // create substring to remove .txt extension
        val fileName = file.substring(0, fileExtensionIndex)
        print("In $fileName...")
            // find the specified word
        findWordInFile(fileName, wordToFind, directoryName)
    }

}

    // add new text to the given file. Takes fileName and DirectoryName arguments
fun appendToFile(fileName: String, directoryName: String){
        // get string list of all file names in the specified directory and checks for the fileName
    if(getListOfAllFiles(directoryName, print=false).contains("$fileName.txt")){
        val path = "$directoryName/$fileName.txt" // create filePath string
        while(true) { // while loop
            print("$directoryName/$fileName editing:>> ")
            val userTextInput = readln() // read user input to string
                // break the loop if the user enters quit, ignoring case
            if(userTextInput.equals("quit", ignoreCase = true)){
                break
            } else {
                val newText = "\n" + userTextInput // concatenate user string input onto newline character
                try { // try block attempts to append to file.
                        // write string converted to byteArray to the file at the given filepath
                    Files.write(Paths.get(path), newText.toByteArray(), StandardOpenOption.APPEND)
                } catch (_: IOException) {
                }
            }
        }
            // print to user that the file was not find the specified directory
    } else {
        println("Invalid input: File does not exist")
        return
    }
}

    // grab list of filenames in specified directory. Takes directoryName string argument, and print boolean, to either
    // print the list of file names in the function or simply return the list.
fun getListOfAllFiles(directoryName: String, print: Boolean): List<String> {
    var filesInMemory: List<String> = emptyList() // initialize empty list
    val absolutePath = Paths.get("").toAbsolutePath().toString() // empty string to fill with directory path
    val resourcePath = Paths.get(absolutePath, directoryName) // build directory path to search in
    val paths = Files.walk(resourcePath) // walk through each element of the resourcePath
        .filter { item -> Files.isRegularFile(item) } // filter out directories and symbolic links
        .filter { item -> item.toString().endsWith(".txt") } // filter out files with .txt extension
        .use { stream -> // step through each item collected by the above filter lines.
            stream.forEach { item ->
                    // grab the fileName separate from the absolute file path
                val fileName = resourcePath.relativize(item).toString()
                    // add the file name in the form "fileName.txt" to the list of fileNames
                filesInMemory = filesInMemory + fileName
            }
        }
            // print each fileName in the list of file names if the print boolean argument is true
        if(print){
            for(file in filesInMemory) {
                println("     $file")
            }
        }
    return filesInMemory // return list of fileName
}

// take a fileName string and directoryName string, and read in a file as a list of strings, each string being a line
fun readFileAsLines(fileName: String, directoryName: String): List<String> {
    val path = "$directoryName/$fileName.txt" // build filePath string
    return File(path).readLines() // return list of line strings
}

    // grabs string list of file lines similar to readFileAsLines function. but prints each line.
    // I realize now that I could remove this function and add print functionality to the readFileAsLines function
    // similar to the getListOfAllFiles function, but I dont have the wherewithall at this point.
    // TODO( remove this function, and add print functionality to the readFileAsLines function)
fun printFile(fileName: String, directoryName: String) {
    val path = "$directoryName/$fileName.txt"
    val fileContents = File(path).readLines()
    for(line in fileContents) {
        println("     $line")
    }
}

    // take user input string and remove commands, file names, and argument
fun parseInput(userCommandInput: String): Triple<String, String, String> {
    // function steps through each character in the input string and counts
    fun findNumSpaces(userCommandInput: String): Int {
            var numSpaces = 0 // initialize number of spaces variable
            // step through the characters in the input string and count the number of spaces
        for(char in userCommandInput) {
            if(char == ' '){
                ++numSpaces // increment number of spaces variable
            }
        }
        return numSpaces // return the number of spaces
    }
        // checks that the input command is more than one word, by checking for a space
    if(userCommandInput.contains(' ')){
        val numSpaces = findNumSpaces(userCommandInput) // grab number of spaces
        if(numSpaces == 1){ // if 1 space, then there are 2 words
                // split the input string into two words as a list of 2 strings
            val parts = userCommandInput.split(' ', limit = 2)
                // grab the string at index 0 as the command
            val command = parts.getOrElse(0) { "" }.trim()
                // grab the string at index 1 as the fileName
            val fileName = parts.getOrElse(1) { "" }.trim()
                // return a triple with command, fileName, and null for the argument string
            return Triple(command, fileName, "null")
        } else if(numSpaces == 2){ // if 2 spaces, then there are 3 words
                // split the input string into three words as a list of 3 strings
            val parts = userCommandInput.split(' ', limit = 3)
                // grab the string at index 0 as the command
            val command = parts.getOrElse(0) { "" }.trim()
                // grab the string at index 1 as the fileName
            val fileName = parts.getOrElse(1) { "" }.trim()
                // grab the string at index 2 as the argument
            val argument = parts.getOrElse(2) { "" }.trim()
            return Triple(command, fileName, argument) // return all three strings
        }
    }
        // simple return the input string without any checking or modification
    return Triple(userCommandInput, "null", "null")
}

    // take a fileName argument and delete it
fun deleteFile(fileName: String) {
    val filePath = Paths.get("text_files/$fileName.txt") // create filePath
    try { // attempt to delete the specified file if it exists
        val deleteResult = Files.deleteIfExists(filePath)
        println("$fileName deleted")
    } catch (e: IOException) {
        // exception triggers if the file does not exist or if some error occured
        println("Deletion failed, or file does not exist")
        e.printStackTrace()
    }
}

    // take directoryName string argument and create a directory
fun createDirectory(directoryName: String) {
        // the logic statement in this if block attempts to create a directory with the specified
        // name and returns true or false.
    if(File(directoryName).mkdir()){
        println("Directory created successfully") // print success to user
    } else {
        println("failed to create $directoryName directory") // print failure to user
    }
}

    // take directoryName string argument and delete the directory
fun deleteDirectory(directoryName:String) {
        // check if the directory name argument is equal to the default file directory and prevent deletion
    if(directoryName == "text_files"){
        println("cannot delete default directory")
        return
    }
    val fullPath = File(directoryName).absolutePath // create absolute directory file path
    println("checking for: $fullPath")
    val dirToDel = File(fullPath) // directory to delete absolute filepath

        // if directory does not exist, exit function
    if (!dirToDel.exists()) {
        println("Directory does not exist")
        return
    }
        // grab list of files in directory to check if files exist
    val filesInDir = getListOfAllFiles(directoryName, print = false)
    println("Directory exists")
    if(filesInDir.isEmpty()){ // if no files in the directory delete directory with no further prompts to user
        try{
            dirToDel.deleteRecursively() // deletes directory and all files containing,(whether there are files or not)
            println("directory deleted")
        } catch (e: Exception){
            e.printStackTrace()
        }
    } else { // if files exist in the directory, prompt user to abort or continue
        println("You are about to delete a directory along with existing files.")
        println("Do you wish to proceed? yes or no")
        val delYorN = readlnOrNull()?.trim()?.lowercase() // read user input
        when(delYorN){ // when block for yes and no case of delYorN variable
            "yes" -> try{ // attempt to delete directory
                dirToDel.deleteRecursively()
                println("directory deleted")
            } catch (e: Exception){
                e.printStackTrace()
            }
            "no" -> println("aborting directory delete") // print abort to user and exit function
        }
    }
}

    // main menu function
fun menu(){
        // initialilze directory as the default text_files
    var currentDirectory = "text_files" // default directory
        // menu command options string. prints once at start, and can be displayed with the help command
    val menuOptions:String = "menu options:\n" +
            " mkdir as \"file name(one word)\" = create the specified directory\n" +
            "         cd to \"directory name\" = change to specified directory\n" +
            "        searchfile \"file name\" \"word\" = search for a key word in a specified file\n" +
            "           searchdir as \"word\" = search the active directory for a key word\n" +
            "               read \"file name\" = print the contents of a file\n" +
            "            newfile \"file name\" = create new file\n" +
            "             append \"file name\" = add new lines of text to existing file\n" +
            "             deletefile \"file name\" = delete the specified file\n" +
            "             deleteir \"directory Name\" = delete specified directory\n" +
            "                        listall = print list of all file names\n" +
            "                           quit = exit program\n" +
            "                           help = display command options"

    println(menuOptions) // print menu command options
    mainLoop@ while(true) { // menu loop
        print("\n$currentDirectory/menu >> ")
        val userCommandString = readlnOrNull()?.trim()?.lowercase()?: break // read in user input
            // parse user input string into command, fileName and argument strings
        val(command, fileName, argument) = parseInput(userCommandString)
        when(command) {
                // create new file
            "newfile" -> createFile(fileName, currentDirectory)
                // list all files in a directory and print
            "listall" -> getListOfAllFiles(currentDirectory, print=true)
                // append to a file
            "append" -> appendToFile(fileName, currentDirectory)
                // print a specified file
            "read" -> printFile(fileName, currentDirectory)
                // find a word in a file
            "searchfile" -> findWordInFile(fileName, argument, currentDirectory)
                // find a word in all files in a specified directory
            "searchdir" -> findWordInDirectory(argument, currentDirectory)
                // delete a specified file
            "deletefile" -> deleteFile(fileName)
                // delete a specified directory
            "deletedir" -> try{
                                deleteDirectory(fileName) // call to delete directory
                                        // if the deleted directory is the active directory and not the default dir
                                     if (currentDirectory == fileName && currentDirectory != "text_files") {
                                            println("Active directory deleted, switched to default directory")
                                            currentDirectory = "text_files"
                                     }
                            } catch(e: Exception){
                            println("error")
                            }
                // creates a directory with the specified name
            "mkdir" -> createDirectory(argument)
                // change to the specified directory
            "cd" -> currentDirectory = argument
                // end the program
            "quit" -> break@mainLoop
                // display the menu command options
            "help" -> println(menuOptions)
        }
    }
}
fun main() {
    val textFilesDirectory = "text_files" // default directory name string
    val file = File(textFilesDirectory) // creates File from the directorName
    if(file.isDirectory) { // checks if the directory exists
        println("Directory exists")
    } else { // creates the default directory if it does not exist
        println("Directory not found. Creating directory")
        createDirectory(textFilesDirectory)
    }
    menu() // call menu function
 }