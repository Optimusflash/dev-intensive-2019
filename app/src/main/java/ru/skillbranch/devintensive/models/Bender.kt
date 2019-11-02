package ru.skillbranch.devintensive.models

class Bender(var status: Status = Status.NORMAL, var question: Question = Question.NAME) {

    fun askQuestion(): String = when(question) {
                Question.NAME -> Question.NAME.question
                Question.PROFESSION -> Question.PROFESSION.question
                Question.MATERIAL -> Question.MATERIAL.question
                Question.BDAY -> Question.BDAY.question
                Question.SERIAL -> Question.SERIAL.question
                Question.IDLE -> Question.IDLE.question
    }

    fun listenAnswer(answer: String): Pair<String, Triple<Int, Int, Int>> {

         return when{
             !question.isValidate(answer).first -> "${question.isValidate(answer).second}\n${question.question}" to status.color
             question.answers.contains(answer.toLowerCase().trim()) -> {
                 question = question.nextQuestion()
                 "Отлично - ты справился\n${question.question}" to status.color
             }
             question == Question.IDLE -> question.question to status.color
             else -> {
                 if (status != Status.CRITICAL){
                     status = status.nextStatus()
                     "Это неправильный ответ\n${question.question}" to status.color
                 } else{
                     question = Question.NAME
                     status = Status.NORMAL
                     "Это неправильный ответ. Давай все по новой\n${question.question}" to status.color
                 }
             }
         }

         /*if (question.answers.contains(answer)){
                question = question.nextQuestion()
                return "Отлично - ты справился\n${question.question}" to status.color

        } else{
            if (status != Status.CRITICAL){
                status = status.nextStatus()
                return "Это не правильный ответ\n ${question.question}" to status.color
            } else{
                question = Question.NAME
                status = Status.NORMAL
                return "Это неправильный ответ. Давай все по новой\n ${question.question}" to status.color
            }
        }*/
    }

    enum class Status(val color: Triple<Int, Int, Int>) {
        NORMAL(Triple(255, 255, 255)),
        WARNING(Triple(255, 120, 0)),
        DANGER(Triple(255, 60, 60)),
        CRITICAL(Triple(255, 0, 0));

        fun nextStatus(): Status{
            return if (this.ordinal< values().lastIndex){
                values()[this.ordinal +1]
            } else{
                values()[0]
            }
        }
    }

    enum class Question(val question: String, val answers: List<String>) {
        NAME("Как меня зовут?", listOf("бендер", "bender")) {
            override fun isValidate(answer: String): Pair<Boolean,String> {
                return (answer.trim().firstOrNull()?.isUpperCase() ?: false) to
                "Имя должно начинаться с заглавной буквы"
            }

            override fun nextQuestion(): Question  = PROFESSION
        },
        PROFESSION("Назови мою профессию?", listOf("сгибальщик", "bender")) {
            override fun isValidate(answer: String): Pair<Boolean, String> {
                return (answer.trim().firstOrNull()?.isLowerCase() ?: false) to
                "Профессия должна начинаться со строчной буквы"
            }

            override fun nextQuestion(): Question = MATERIAL
        },
        MATERIAL("Из чего я сделан?", listOf("металл", "дерево", "metal", "iron", "wood")) {
            override fun isValidate(answer: String): Pair<Boolean, String> {
               return answer.trim().contains(Regex("\\d")).not() to
               "Материал не должен содержать цифр"
            }
            override fun nextQuestion(): Question = BDAY
        },
        BDAY("Когда меня создали?", listOf("2993")) {
            override fun isValidate(answer: String): Pair<Boolean, String> {
                return answer.trim().contains(Regex("^[0-9]*$")) to
                "Год моего рождения должен содержать только цифры"
            }
            override fun nextQuestion(): Question  = SERIAL
        },
        SERIAL("Мой серийный номер?", listOf("2716057")) {
            override fun isValidate(answer: String): Pair<Boolean, String> {
                return  answer.trim().contains(Regex("^[0-9]{7}$")) to
                "Серийный номер содержит только цифры, и их 7"
            }
            override fun nextQuestion(): Question  = IDLE
        },
        IDLE("На этом все, вопросов больше нет", listOf()) {
            override fun isValidate(answer: String): Pair<Boolean, String> {
               return true to ""
            }

            override fun nextQuestion(): Question = IDLE
        };

        abstract fun nextQuestion(): Question
        abstract fun isValidate(answer: String): Pair<Boolean,String>
    }
}