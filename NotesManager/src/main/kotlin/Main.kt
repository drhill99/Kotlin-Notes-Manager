import java.util.Scanner
import java.io.File
import java.io.BufferedWriter
import java.io.BufferedReader
import java.io.FileWriter

fun createFile(): File {
    println("Creating a new file.\n")
    print("Enter a file name:")
    val fileName = readln()
    val filePath ="text_files/$fileName.txt"
    val file = File(filePath)
    writeToFile(file)
    return file
}

fun writeToFile(file: File) {
    print("input:")
    val userTextInput = readln()
    try {
        // create writer pointing at the desired file
        val writer = BufferedWriter(FileWriter(file))
        // write to the file
        writer.write(userTextInput)
        // kill the writer object
        writer.close()
        println("Text successfully saved to file")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
fun main(args: Array<String>) {
    //TODO( implement the opening and editing of a file)
    //TODO( implement file reading)
    val userInputReader = Scanner(System.`in`)
    var files: List<File> = emptyList()
    do {
        println("1. create and write to new file.")
        println("2. open existing file.")
        println("3. print list of existing files.")
        println("0. exit menu.")
        val userMenuInput = userInputReader.nextInt()
        if (userMenuInput == 1) {
            val newFile = createFile()
            // add file to list
            files = files + newFile

        } else if (userMenuInput == 2){
            //TODO( fill out if statment)
            println("userMenuInput == 2")
        } else if (userMenuInput == 3) {
            println(files)
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