//package org.example.tgClient
//
//import java.util.*
//import java.util.regex.Pattern
//
///*
//TODO:
//1) добавить поддержку обмена хттп между клиентом и сервером
//2) написать метод для отправки/получения запросов
//3) написать метод обработки ответа
//4) метод для работы с пользователем
// */
//
//class CLI {
//
//    fun start() {
//        val city = readCity()
//
//
//    }
//
//    private fun readCity(): String {
//        val scanner = Scanner(System.in)
//        //регулярка для первичной проверки на название города
//
//        val regex = "^[A-Za-z\\s-]+(?:[\\s-][A-Za-z\\s-]+)*$"
//        var possibleName: String
//
//        while (true) {
//            try {
//                possibleName = scanner.next()
//                val matches = Pattern.matches(regex, possibleName)
//                if (matches) {
//                    return possibleName
//                } else {
//                    println("Некорректное название города. Попробуйте еще раз")
//                }
//            } catch (ignore: NoSuchElementException) {
//            }
//        }
//    }
//
//    private fun sendResponse(city: String) {
//
//    }
//}
