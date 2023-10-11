import java.io.*
import java.util.Scanner
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption


fun createFile(): File {
    println("Creating a new file.\n")
    print("Enter a file name:")
    val fileName = readln()
    val filePath ="text_files/$fileName.txt"
    val file = File(filePath)
    writeToFile(file)
    return file
}

fun findWordInFile() {
    val fileAsStrings = readFileAsLines()
    print("Enter word to find: ")
    val wordToFind:String = readln()
    outerLoop@ for(line in fileAsStrings) {
        innerLoop@ for(letter in line) {
            if(letter == wordToFind[0]) {
                val checkedLetter = wordToFind[0]
//                println("found $checkedLetter")
                // println(line)
                var testWord: String = ""
                val firstLetterIndex = line.indexOf(letter)

                for(i in wordToFind.indices){
                    testWord += line[i+firstLetterIndex]
                }
//                println(testWord)
                if(testWord == wordToFind) {
//                    println("found $wordToFind")
                    println("line containing $wordToFind : $line")
                    // break the inner loop if a line containing the word is found.
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

fun appendToFile(){
    print("Enter file to write to: ")
    val fileName = readln()
    val path = "text_files/$fileName"
    print("Enter new text: ")
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
    val fileContents = File(path).readLines()
//    for(line in fileContents) {
//        println(line)
//        for(word in line) {
//            println(word)
//
//        }
////        println(line)
//    }
    return fileContents
}


fun main(args: Array<String>) {
    //TODO( implement the opening and appending to a file) DONE
    //TODO( implement the opening and editing of a file)
    //TODO( implement file reading)
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
        if (userMenuInput == 1) {
            val newFile = createFile()
            // add file to list
            openfiles = openfiles + newFile

        } else if (userMenuInput == 2){
            //TODO( fill out if statment)
            getListOfAllFiles()
        } else if (userMenuInput == 3) {
            println(openfiles)
        } else if (userMenuInput == 4) {
            appendToFile()
        } else if (userMenuInput == 5) {
            readFileAsLines()
        } else if (userMenuInput == 6) {
            findWordInFile()
        }

    } while(userMenuInput != 0)



//    print("Enter a file name, not including the extension: ")
//    val fileName = readln()
//    val filePath = "text_files/$fileName.txt"
//
//    print("Enter a string:")
//    val userInputString = readln()
////    println(userInputString)
//    val file = File(filePath)




}