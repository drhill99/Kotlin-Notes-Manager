import java.io.*
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

fun createFile(fileName: String, directoryName: String) {
    println("Creating a new file.\n")
    var filePath = "$directoryName/$fileName.txt"
    filePath = filePath.trim()
    val file = File(filePath)
    print("$directoryName/$fileName >> ")
    val userTextInput = readln()
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
    appendToFile(fileName, directoryName)
}

fun findWordInFile(fileName: String, wordToFind: String, directoryName: String) {

    if(getListOfAllFiles(directoryName, print=false).contains("$fileName.txt")){
        println("finding references to \"$wordToFind\".........")
        val fileAsStrings = readFileAsLines(fileName, directoryName)
        var wordFound = false
        outerLoop@ for ((lineNumber, line) in fileAsStrings.withIndex()) {
            innerLoop@ for (i in line.indices) {
                if (i + wordToFind.length > line.length) {
                    // Skip if the remaining characters are less than the word length
                    continue@outerLoop
                }
                val testWord = line.substring(i, i + wordToFind.length).lowercase()
                if ((i + wordToFind.length) < line.length) {
                    if (testWord == wordToFind && (i == 0 || line[i - 1] == ' ') && line[i + wordToFind.length] == ' ') {
                        wordFound = true
                        println("   Line:${lineNumber + 1}   $line")
                        // break the inner loop if the word is found early in the line
                        break@innerLoop
                    }
                } else {
                    if (testWord == wordToFind && (i == 0 || line[i - 1] == ' ')) {
                        wordFound =true
                        println("Line: ${lineNumber + 1} $line")
                        // break the inner loop if the word is found early in the line
                        break@innerLoop
                    }
                }
            }
        }
        if(!wordFound){
            println("No references to \"$wordToFind\" found.")
        }
    } else {
        println("invalid input: file does not exist")
        return
    }
}

fun findWordInDirectory(wordToFind: String, directoryName: String){
    val listOfFilesInDir = getListOfAllFiles(directoryName, print = false)
    for(file in listOfFilesInDir){
        val fileExtensionIndex = file.indexOf('.')
        val fileName = file.substring(0, fileExtensionIndex)
        print("In $fileName...")
        findWordInFile(fileName, wordToFind, directoryName)
    }

}

fun appendToFile(fileName: String, directoryName: String){
    if(getListOfAllFiles(directoryName, print=false).contains("$fileName.txt")){
        val path = "$directoryName/$fileName.txt"
        while(true) {
            print("$directoryName/$fileName >> ")
            val userTextInput = readln()
            if(userTextInput.equals("quit", ignoreCase = true)){
                break
            } else {
                val newText = "\n" + userTextInput
                try {
                    Files.write(Paths.get(path), newText.toByteArray(), StandardOpenOption.APPEND)
                } catch (_: IOException) {
                }
            }
        }
    } else {
        println("Invalid input: File does not exist")
        return
    }
}

fun getListOfAllFiles(directoryName: String, print: Boolean): List<String> {
    var filesInMemory: List<String> = emptyList()
    val absolutePath = Paths.get("").toAbsolutePath().toString()
    val resourcePath = Paths.get(absolutePath, directoryName)
    val paths = Files.walk(resourcePath)
        .filter { item -> Files.isRegularFile(item) }
        .filter { item -> item.toString().endsWith(".txt") }
        .use { stream ->
            stream.forEach { item ->
                val fileName = resourcePath.relativize(item).toString()
                filesInMemory = filesInMemory + fileName
            }
        }
        if(print){
            for(file in filesInMemory) {
                println("     $file")
            }
        }
    return filesInMemory
}
fun readFileAsLines(fileName: String, directoryName: String): List<String> {
    val path = "$directoryName/$fileName.txt"
    return File(path).readLines()
}

fun printFile(fileName: String, directoryName: String) {
    val path = "$directoryName/$fileName.txt"
    val fileContents = File(path).readLines()
    for(line in fileContents) {
        println("     $line")
    }
}

// take user input string and remove commands and file names
fun parseInput(userCommandInput: String): Triple<String, String, String> {
    fun findNumSpaces(userCommandInput: String): List<Int> {
        var spaceIndexes: List<Int> = emptyList()
        for(char in userCommandInput) {
            val charIndex = 0
            if(char == ' '){
                spaceIndexes = spaceIndexes + charIndex
            }
        }
        return spaceIndexes
    }
    if(userCommandInput.contains(' ')){
        val spaceIndexes = findNumSpaces(userCommandInput)
        if(spaceIndexes.size == 1){
            val parts = userCommandInput.split(' ', limit = 2)
            val command = parts.getOrElse(0) { "" }.trim()
            val fileName = parts.getOrElse(1) { "" }.trim()
            return Triple(command, fileName, "null")
        } else if(spaceIndexes.size == 2){
            val parts = userCommandInput.split(' ', limit = 3)
            val command = parts.getOrElse(0) { "" }.trim()
            val fileName = parts.getOrElse(1) { "" }.trim()
            val argument = parts.getOrElse(2) { "" }.trim()
            return Triple(command, fileName, argument)
        }
    }
    return Triple(userCommandInput, "null", "null")
}

fun deleteFile(fileName: String) {
    val filePath = Paths.get("text_files/$fileName.txt")
    try {
//        val deleteResult = Files.deleteIfExists(filePath)
        println("$fileName deleted")
    } catch (e: IOException) {
        println("Deletion failed")
        e.printStackTrace()
    }
}

fun createDirectory(directoryName: String) {
    if(File(directoryName).mkdir()){
        println("Directory created successfully")
    } else {
        println("failed to create $directoryName directory")
    }
}

fun deleteDirectory(directoryName:String) {
    if(directoryName == "text_files"){
        println("cannot delete default directory")
        return
    }
    if(directoryName == null) {
        return
    }
    val fullPath = File(directoryName).absolutePath
    println("checking for: $fullPath")
    val dirToDel = File(fullPath)

    if (!dirToDel.exists()) {
        println("Directory does not exist")
        return
    }
    val filesInDir = getListOfAllFiles(directoryName, print = false)
    println("Directory exists")
    if(filesInDir.isEmpty()){
        try{
            dirToDel.deleteRecursively()
            println("directory deleted")
        } catch (e: Exception){
            e.printStackTrace()
        }
    } else {
        println("You are about to delete a directory along with existing files.")
        println("Do you wish to proceed? yes or no")
        val delYorN = readlnOrNull()?.trim()?.lowercase()
        when(delYorN){
            "yes" -> try{
                dirToDel.deleteRecursively()
                println("directory deleted")
            } catch (e: Exception){
                e.printStackTrace()
            }
            "no" -> println("aborting directory delete")
        }
    }
}
fun menu(){
    var currentDirectory = "text_files" // default directory
    val menuOptions:String = "menu options:\n" +
            " mkdir as \"file name(one word)\" = create the specified directory\n" +
            "         cd to \"directory name\" = change to specified direcory\n" +
            "        find \"file name\" \"word\" = search for a key word in a specified file\n" +
            "           findall indir \"word\" = search the active directory for a key word\n" +
            "               read \"file name\" = print the contents of a file\n" +
            "            newfile \"file name\" = create new file\n" +
            "             append \"file name\" = add new lines of text to existing file\n" +
            "             deletefile \"file name\" = delete the specified file\n" +
            "             deleteir \"directory Name\" = delete specified directory\n" +
            "                        listall = print list of all file names\n" +
            "                           quit = exit program\n" +
            "                           help = display command options"

    println(menuOptions)
    mainLoop@ while(true) {
        print("\n$currentDirectory/menu >> ")
        val userCommandString = readlnOrNull()?.trim()?.lowercase()?: break
        val(command, fileName, argument) = parseInput(userCommandString)
        when(command) {
            "newfile" -> createFile(fileName, currentDirectory)
            "listall" -> getListOfAllFiles(currentDirectory, print=true)
            "append" -> appendToFile(fileName, currentDirectory)
            "read" -> printFile(fileName, currentDirectory)
            "find" -> findWordInFile(fileName, argument, currentDirectory)
            "findall" -> findWordInDirectory(argument, currentDirectory)
            "deletefile" -> deleteFile(fileName)
            "deletedir" -> if (currentDirectory == fileName) {
                                deleteDirectory(fileName)
                            if(currentDirectory != "text_files") {
                                println("Active directory deleted, switched to default directory")
                            currentDirectory = "text_files"
                            }
            }

            "mkdir" -> createDirectory(argument)
            "cd" -> currentDirectory = argument
            "quit" -> break@mainLoop
            "help" -> println(menuOptions)
        }
    }
}
fun main() {
    val textFilesDirectory = "text_files"
    val file = File(textFilesDirectory)
    if(file.isDirectory) {
        println("Directory exists")
    } else {
        println("Directory not found. Creating directory")
        createDirectory(textFilesDirectory)
    }
    menu()
 }