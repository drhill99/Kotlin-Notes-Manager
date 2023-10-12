import java.io.*
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.*


fun createFile(fileName: String, directoryName: String) {
//    val inputReader = Scanner(System.`in`)
    println("Creating a new file.\n")
//    print("Enter a file name:")
//    val fileName = readln()
//    val filePath ="text_files/$fileName.txt"
//    val file = File(filePath)
//    writeToFile(fileName)
    var filePath = "$directoryName/$fileName.txt"
    filePath = filePath.trim()
    val file = File(filePath)
    print("$fileName >> ")
    val userTextInput = readln()

    try {
        // create writer pointing at the desired file
        val writer = BufferedWriter(FileWriter(file))
        // write to the file
        writer.write(userTextInput.toString())
        // kill the writer object
        writer.close()
//        println("Text successfully saved to file")
    } catch (e: Exception) {
        e.printStackTrace()
    }
    appendToFile(fileName, directoryName)

}

fun findWordInFile(fileName: String, wordToFind: String, directoryName: String) {
//    println("finding word.....")
    if(getListOfAllFiles(directoryName, print=false).contains("$fileName.txt")){
        println("finding references to \"$wordToFind\".........")
        val fileAsStrings = readFileAsLines(fileName, directoryName)
//      print("Enter word to find: ")
//      val wordToFind: String = readlnOrNull() ?: return

        outerLoop@ for ((lineNumber, line) in fileAsStrings.withIndex()) {
//        println("Current line: $line") // Test code

            innerLoop@ for (i in line.indices) {
                if (i + wordToFind.length > line.length) {
                    // Skip if the remaining characters are less than the word length
                    continue@outerLoop
                }

                val testWord = line.substring(i, i + wordToFind.length).lowercase()
//            val remainingIndices = i + wordToFind.length // test code
//            println("remaining indices: $remainingIndices") // test code
//            val lineLength = line.length // test code
//            println("line length: $lineLength") // test code
                if ((i + wordToFind.length) < line.length) {
                    if (testWord == wordToFind && (i == 0 || line[i - 1] == ' ') && line[i + wordToFind.length] == ' ') {
//                    println("Found $wordToFind")
                        println("   Line:${lineNumber + 1}   $line")
                        // break the inner loop if the word is found early in the line
                        break@innerLoop
                    }
                } else {
                    if (testWord == wordToFind && (i == 0 || line[i - 1] == ' ')) {

                        println("Line: ${lineNumber + 1} $line")
                        // break the inner loop if the word is found early in the line
                        break@innerLoop
                    }
                }
            }
        }
    } else {
        println("invalid input: file does not exist")
        return
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
            val charIndex = 0;
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
        val deleteResult = Files.deleteIfExists(filePath)
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
fun menu(){

    var currentDirectory:String = "text_files" // default directory
    val menuOptions:String = "menu options:\n" +
            "  grep \"file name\" \"word\" = search for a key word in a specified file\n" +
            "    cd / \"directory name\" = change to specified direcory\n" +
            "      mkdir / \"file name\" = create the specified directory\n" +
            "        touch \"file name\" = create new file\n" +
            "       append \"file name\" = add new lines of text to existing file\n" +
            "           rm \"file name\" = delete the specified file\n" +
            "                     more = print the contents of a file\n" +
            "                       ls = print list of all file names\n" +
            "                     quit = exit program\n" +
            "                     help = display command options"

    println(menuOptions)
    mainLoop@ while(true) {

        print("\n$currentDirectory/menu >> ")
        val userCommandString = readlnOrNull()?.trim()?.lowercase()?: break
        val(command, fileName, argument) = parseInput(userCommandString)
        when(command) {
            "touch" -> createFile(fileName, currentDirectory)
            "ls" -> getListOfAllFiles(currentDirectory, print=true)
            "append" -> appendToFile(fileName, currentDirectory)
            "more" -> printFile(fileName, currentDirectory)
            "grep" -> findWordInFile(fileName, argument, currentDirectory)
            "rm" -> deleteFile(fileName)
            "mkdir" -> createDirectory(argument)
            "cd" -> currentDirectory = argument
            "quit" -> break@mainLoop
            "help" -> println(menuOptions)
        }
    }
}
// TODO( implement the option to put a new file in any directory)
fun main(args: Array<String>) {

    val textFilesDirectory = "text_files"
    val file = File(textFilesDirectory)
    if(file.isDirectory) {
        println("Directory exists")
    } else {
        println("Directory not found. Creating directory")
        createDirectory(textFilesDirectory)
    }
    menu()
    //TODO( implement the opening and appending to a file) DONE
    //TODO( implement the opening and editing of a file) DONE
    //TODO( implement file reading) DONE
    //TODO( add error handling)
    //TODO( hide the menu, add command to show the command options, a la linux command line) DONE
    //TODO( implement command parsing ie: print test7. parse out the command and the filename) DONE

    //TODO (research 'when' keyword) DONE


}