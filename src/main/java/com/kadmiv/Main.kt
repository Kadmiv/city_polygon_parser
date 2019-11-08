package com.kadmiv

import io.github.bonigarcia.wdm.WebDriverManager

import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.By
import java.io.*
import java.lang.Thread.sleep
import java.util.ArrayList


//
fun main() {

    WebDriverManager.chromedriver().setup();
    val link = "https://maps.visicom.ua/"

    val driver = ChromeDriver()


//    driver.findElement(By.cssSelector("#search > input.input.microphone"))
    driver.get(link)
    sleep(3000)


    val cities = loadDataFromCSV(File("kiev.csv"))
    val newCitiesTable = arrayListOf<String>()
    val findInput = driver.findElement(By.xpath("//*[@id=\"search\"]/input[2]"))

    val startTime = System.currentTimeMillis()
    cities.forEachIndexed { index, item ->

        //        if(index>10)
//            return@forEachIndexed

        var address = item.replace(";", " ").replace("\"", "")

        findInput.clear()
        findInput.sendKeys(address)
        sleep(300)
        val firstSearchElement = driver.findElement(By.xpath("//*[@id=\"suggest\"]/ul/li[1]"))
        firstSearchElement.click()
        sleep(300)
        val fileName = driver.currentUrl.split("/").last().split("?").first()
        val jsonLink = "https://maps.visicom.ua/proxy/uk/feature/$fileName"

        newCitiesTable.add("$item;\"$jsonLink\"")
//        driver.get(jsonLink)
    }
    println("time:${System.currentTimeMillis() - startTime}")

    saveToFile(newCitiesTable)
    driver.close()
    System.exit(0)


}

fun saveToFile(newCitiesTable: ArrayList<String>) {
    val dataFile = File("new_kiev.csv")
    if (!dataFile.exists())
        dataFile.createNewFile()

    try {
        val bw = BufferedWriter(OutputStreamWriter(FileOutputStream(dataFile), "UTF-8"))
        for (dataItem in newCitiesTable) {
            bw.write(dataItem)
            bw.newLine()
        }
        bw.flush()
        bw.close()
    } catch (e: IOException) {
    }

}

fun loadDataFromCSV(file: File): ArrayList<String> {
    var br: BufferedReader? = null
    var line: String? = ""


    val dataList = arrayListOf<String>()
    try {

        br = BufferedReader(FileReader(file))
        line = br.readLine()

        while (line != null) {
            // use comma as separator
            val data = line
            dataList.add(data)
            line = br.readLine()
        }

    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        if (br != null) {
            try {
                br.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    return dataList
}