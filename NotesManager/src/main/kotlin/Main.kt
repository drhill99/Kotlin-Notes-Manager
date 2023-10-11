import java.io.*
import java.util.Scanner
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption


fun createFile(): File {
    val inputReader = Scanner(System.`in`)
    println("Creating a new file.\n")
    print("Enter a file name:")
    val fileName = readln()
    val filePath ="text_files/$fileName.txt"
    val file = File(filePath)
    writeToFile(file)
    print("new line?: ")
    val userLoopInput = inputReader.next()
    var loopInput = userLoopInput[0]
    while(loopInput.equals('y', ignoreCase = true)){
        appendToFile("$fileName.txt")
        print("new line?: ")
        val userLoopInput2 = inputReader.next()
        loopInput = userLoopInput2[0]
    }

    return file
}

//fun findWordInFile() {
//    val fileAsStrings = readFileAsLines()
//    print("Enter word to find: ")
//    val wordToFind:String = readln()
//    outerLoop@ for(line in fileAsStrings) {
//        println("current line: $line") // test code
//        var firstLetterIndex = -1
//        innerLoop@ for(letter in line) {
//            print("ltr: $letter") // test code
//            var testWord: String = ""
//            firstLetterIndex = line.indexOf(letter)
//            println(" index: $firstLetterIndex")
//            if(letter == wordToFind[0]) {
//                val checkedLetter = wordToFind[0]
//                println("found $checkedLetter")
////                 println(line)
////                println("first letter index: $firstLetterIndex")
//
//                for(i in wordToFind.indices){
//                    val letterIndex = i + firstLetterIndex
////                    println("letterIndex: $letterIndex") // test code
////                    val lineLength = line.length
////                    println("line.length: $lineLength")
//                    if(letterIndex < line.length){
//                        testWord += line[i+firstLetterIndex]
//                    }
//
//                    print("testWord: $testWord ") // test code
//                }
//                println("testWord: $testWord") // test code
//                if(testWord == wordToFind && line[firstLetterIndex - 1] == ' ') {
//                    println("found $wordToFind")
//                    val lineNumber = fileAsStrings.indexOf(line) + 1
//                    println("line: $lineNumber $line")
//                    // break the inner loop if a line containing the word is found.
//                    break@innerLoop
//                }
//            } else {
//                testWord = ""
//                firstLetterIndex = -1
//            }
//        }
//    }
//}
fun findWordInFile() {
    val fileAsStrings = readFileAsLines()
    print("Enter word to find: ")
    val wordToFind: String = readlnOrNull() ?: return

    outerLoop@ for ((lineNumber, line) in fileAsStrings.withIndex()) {
//        println("Current line: $line") // Test code

        innerLoop@ for (i in line.indices) {
            if (i + wordToFind.length > line.length) {
                // Skip if the remaining characters are less than the word length
                continue@outerLoop
            }

            val testWord = line.substring(i, i + wordToFind.length)
//            val remainingIndices = i + wordToFind.length // test code
//            println("remaining indices: $remainingIndices") // test code
//            val lineLength = line.length // test code
//            println("line length: $lineLength") // test code
            if ((i + wordToFind.length) < line.length) {
                if (testWord == wordToFind && (i == 0 || line[i - 1] == ' ') && line[i + wordToFind.length] == ' ') {
//                    println("Found $wordToFind")
                    println("Line: ${lineNumber + 1} $line")
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
}

fun writeToFile(file: File) {
    print("input:")
    val userTextInput = readln()

    try {
        // create writer pointing at the desired file
        val writer = BufferedWriter(FileWriter(file))
        // write to the file
        writer.write(userTextInput.toString())
        // kill the writer object
        writer.close()
        println("Text successfully saved to file")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun appendToFile(fileName: String){
    println(fileName)
    val path = "text_files/$fileName"
    print("> ")
    val userTextInput = readln()
    val newText = "\n" + userTextInput
    try {
        Files.write(Paths.get(path), newText.toByteArray(), StandardOpenOption.APPEND)
    } catch (_: IOException) {
    }
}
fun getListOfAllFiles(): List<String> {
    var filesInMemory: List<String> = emptyList()
    val absolutePath = Paths.get("").toAbsolutePath().toString()
    val resourcePath = Paths.get(absolutePath, "text_files")
    val paths = Files.walk(resourcePath)
        .filter { item -> Files.isRegularFile(item) }
        .filter { item -> item.toString().endsWith(".txt") }
        .use { stream ->
            stream.forEach { item ->
                val fileName = resourcePath.relativize(item).toString()
                filesInMemory = filesInMemory + fileName
            }
        }
        for(file in filesInMemory) {
            println(file)
        }
    return filesInMemory
}
fun readFileAsLines(): List<String> {
    print("Enter file to read from: ")
    val fileName = readln()
    val path = "text_files/$fileName"
    return File(path).readLines()
}

fun printFile() {
    print("Enter file to read from: ")
    val fileName = readln()
    val path = "text_files/$fileName"
    val fileContents = File(path).readLines()
    for(line in fileContents) {
        println(line)
    }
}

fun main(args: Array<String>) {
    //TODO( implement the opening and appending to a file) DONE
    //TODO( implement the opening and editing of a file) DONE
    //TODO( implement file reading) DONE
    val userInputReader = Scanner(System.`in`)
    var openfiles: List<File> = emptyList()
    do {
        println("1. create and write to new file.")
        println("2. print existing files in memory.")
        println("3. print list of open files.")
        println("4. Add to existing file.")
        println("5. read contents of a file.")
        println("6. search a file for a word.")
        println("0. exit menu.")
        val userMenuInput = userInputReader.nextInt()
        // create and write to new file
        // TODO (research 'when' keyword)
        if (userMenuInput == 1) {
            val newFile = createFile()
            // add file to list
            openfiles = openfiles + newFile
        // print list of files in memory
        } else if (userMenuInput == 2){
            getListOfAllFiles()
        // print list of open files
        } else if (userMenuInput == 3) {
            println(openfiles)
        // append to existing file
        } else if (userMenuInput == 4) {
            print("Enter name of file: ")
            val fileName = readln()
            appendToFile(fileName)
        // read contents of a file
        } else if (userMenuInput == 5) {
            printFile()
        // word search a file
        } else if (userMenuInput == 6) {
            findWordInFile()
        }
    } while(userMenuInput != 0)

}