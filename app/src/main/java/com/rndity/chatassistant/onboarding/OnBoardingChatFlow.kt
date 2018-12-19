/*
 * Created and developed by RnDity sp. z o.o. in 2018.
 * Copyright © 2018 RnDity sp. z o.o. All rights reserved.
 */

package com.rndity.chatassistant.onboarding

import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.rndity.chatassistant.Item
import com.rndity.chatassistant.ItemId
import com.rndity.chatassistant.utils.Counter
import com.rndity.chatassistant.ChatFlow
import com.rndity.chatassistant.FlowPresenter
import com.rndity.chatassistant.animator.add.FromRightAddChatItemAnimator

class OnBoardingChatFlow(presenter: FlowPresenter) : ChatFlow(presenter) {

    private var dummyUserName: String? = null
    private val dummyHandler = Handler(Looper.getMainLooper())

    //TODO: some mechanism to restoring counter
    private var nextIdx: Int by Counter(0)

    override fun start() {
        val nickQuestion = QuestionHeaderItem(
                id = ItemId(ID_NICK_QUESTION, nextIdx),
                text = "Cześć jestem Twoim Botem,\nProszę przedstaw się " + getEmojiByUnicode(0x1F4AA))

        val nickAnswer = TextEnterItem(
                id = ItemId(ID_NICK_ANSWER, nextIdx),
                text = null,
                hint = "Wpisz swój nick")


        presenter.addItems(nickQuestion) {
            presenter.addItems(nickAnswer, delay = 1500,  animator = FromRightAddChatItemAnimator())
        }
    }

    fun proceedWithAnswerEntered(item: Item, text: String) {
        when (item.id.id) {
            ID_NICK_ANSWER -> proceedWithNick(text)
            ID_LOGIN_EMAIL -> proceedLoginWithMail(text)
            ID_LOGIN_PASS -> proceedLoginWithPass(text)
        }
    }

    fun proceedOptionSelected(item: Item, option: RadioOption) {
        when (item.id.id) {
            ID_NICK_RETRY_QUESTION -> proceedWithNickSelected(option.text)
            ID_LOGIN_QUESTION -> proceedWithAuthLogin(option)
            ID_GOOGLE_FIT_QUESTION -> proccedWithGoogleFitSelected()
            ID_LOGIN_CONDITION_QUESTION -> proceedWithRegulationSelected(option)
            ID_ADD_LOYALITY_QUESTION -> proccedWithLoyalityOption(option)
        }
    }

    private fun proceedWithNickSelected(text: String) {
        proceedWithNick(text)
    }

    private fun proceedWithAuthLogin(option: RadioOption) {
        when (option.id) {
            ID_LOGIN_OPTION_GOOGLE -> {
                presenter.addItems(TextFixedItem(ItemId(ID_NICK_FIXED, nextIdx), option.text), animator = FromRightAddChatItemAnimator()) {
                    proccedWithGoogleFit(delay = 1000)
                }
            }
            ID_LOGIN_OPTION_FACEBOOK -> {
                presenter.addItems(TextFixedItem(ItemId(ID_NICK_FIXED, nextIdx), option.text), animator = FromRightAddChatItemAnimator()) {
                    proccedWithGoogleFit(delay = 1000)
                }
            }
            ID_LOGIN_OPTION_INSTAGRAM -> {
                presenter.addItems(TextFixedItem(ItemId(ID_NICK_FIXED, nextIdx), option.text), animator = FromRightAddChatItemAnimator()) {
                    proccedWithGoogleFit(delay = 1000)
                }
            }
            ID_LOGIN_OPTION_MAIL -> {
                presenter.addItems(TextFixedItem(ItemId(ID_NICK_FIXED, nextIdx), option.text), animator = FromRightAddChatItemAnimator()) {
                    presenter.keepsLastItems(1, delay = 1000) {
                        presenter.addItems(TextEnterItem(id = ItemId(ID_LOGIN_EMAIL, nextIdx), hint = "Użyj adresu email"),
                                    animator = FromRightAddChatItemAnimator())
                    }
                }
            }
        }
    }

    private fun proceedWithNick(text: String) {
        presenter.replaceLastItem(TextFixedItem(ItemId(ID_NICK_FIXED, nextIdx), text)) {
            dummyHandler.postDelayed({
                if (text != "taken") {
                    dummyUserName = text
                    processWithNickVerificationSuccess()
                } else {
                    processWithNickVerificationFailure()
                }
            }, 1000)
        }
    }

    private fun proceedLoginWithMail(text: String) {
        presenter.replaceLastItem(TextFixedItem(ItemId(ID_LOGIN_FIXED, nextIdx), text)) {
            presenter.addItems(TextFixedItem(ItemId(ID_NICK_FIXED, nextIdx), "Hasło:"), animator = FromRightAddChatItemAnimator()) {
                presenter.addItems(TextEnterItem(id = ItemId(ID_LOGIN_PASS, nextIdx), hint = "Hasło"),
                        animator = FromRightAddChatItemAnimator())

            }
        }
    }

    private fun proceedLoginWithPass(text: String) {
        presenter.replaceLastItem(TextFixedItem(ItemId(ID_LOGIN_PASS_FIXED, nextIdx), text)) {
            proccedWithGoogleFit(delay = 0)
        }
    }

    private fun processWithNickVerificationSuccess() {
        val question = RadioOptionsItem(
                id = ItemId(ID_LOGIN_QUESTION, nextIdx),
                question = "${dummyUserName}. Teraz poproszę Cię abyś wybrał w jaki sposób chcesz się zalogować",
                options = listOf(RadioOption(ID_LOGIN_OPTION_GOOGLE, "Google"), RadioOption(ID_LOGIN_OPTION_FACEBOOK, "Facebook"), RadioOption(ID_LOGIN_OPTION_INSTAGRAM, "Instagram"),
                        RadioOption(ID_LOGIN_OPTION_MAIL, "E-mail")))
        presenter.keepsLastItems(1) {
            presenter.addItems(question) {
            }
        }
    }

    private fun processWithNickVerificationFailure() {
        val question = RadioOptionsItem(
                id = ItemId(ID_NICK_RETRY_QUESTION, nextIdx),
                question = "Bardzo mi przykro ale ten Nick jest już zajęty. Spróbuj wybrać inny.",
                options = listOf(RadioOption(0, "Kuba2"), RadioOption(1, "KKuba"), RadioOption(2, "Cuba")))
        val answer = TextEnterItem(
                id = ItemId(ID_NICK_ANSWER, nextIdx),
                hint = "Wpisz nowy Nick")

        presenter.keepsLastItems(1) {
            presenter.addItems(question) {
                presenter.addItems(answer, delay = 2500, animator = FromRightAddChatItemAnimator())
            }
        }
    }

    private fun proccedWithGoogleFit(delay: Long) {
        val question = RadioOptionsItem(
                id = ItemId(ID_GOOGLE_FIT_QUESTION, nextIdx),
                question = "Aby korzystać z aplikacji\nkonieczne jest połączenie z\nGoogle Fit.",
                options = listOf(RadioOption(ID_GOOGLE_FIT_OPTION, "Wł. Google Fit")))
        presenter.clearItems(delay = delay) {
            presenter.addItems(question) {
            }
        }
    }

    private fun proccedWithGoogleFitSelected() {
        val answer = TextFixedItem(ItemId(ID_GOOGLE_FIT_ANSWER, nextIdx), "Połącz z Google Fit")
        val superQuestion = QuestionHeaderItem(
                id = ItemId(ID_SUPER_QUESTION, nextIdx),
                text = "Super")
        presenter.addItems(answer, animator = FromRightAddChatItemAnimator()) {
            presenter.addItems(superQuestion) {
                presenter.clearItems(delay = 2500) {
                    proccedWithRegulation()
                }
            }
        }
    }

    private fun proccedWithRegulation() {
        val question = RadioOptionsItem(
                id = ItemId(ID_LOGIN_CONDITION_QUESTION, nextIdx),
                question = "Korzystając z aplikacji Boomerun, zgadzasz się na:",
                options = listOf(RadioOption(ID_LOGIN_CONDITION_REGULATION, "Regulamin serwisu"), RadioOption(ID_LOGIN_CONDITION_RODO, "RODO")))
        presenter.addItems(question) {
        }
    }

    private fun proceedWithRegulationSelected(option: RadioOption) {
        when (option.id) {
            ID_LOGIN_CONDITION_REGULATION -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.rndity.com"))
                //todo: send info to mainactivity
                //startActivity(intent)

                //todo: line below only for test
                presenter.clearItems {
                    proccedWithLoyality()
                }
            }
            ID_LOGIN_CONDITION_RODO -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.rndity.com"))
                //todo: send info to mainactivity
                //startActivity(intent)
            }
        }
    }

    private fun proccedWithLoyality() {
        val question = RadioOptionsItem(
                id = ItemId(ID_ADD_LOYALITY_QUESTION, nextIdx),
                question = "Czy chcesz dodać jakiś program lojalnościowy?",
                options = listOf(RadioOption(ID_ADD_LOYALITY_OPTION_BIEDRONKA, "Biedronka"), RadioOption(ID_ADD_LOYALITY_OPTION_ROSSMAN, "Rossman")
                        , RadioOption(ID_ADD_LOYALITY_OPTION_PAYBACK, "Payback")))
        presenter.addItems(question) {
        }
    }

    private fun proccedWithLoyalityOption(option: RadioOption) {
        when (option.id) {
            ID_ADD_LOYALITY_OPTION_BIEDRONKA -> {
            //todo: add card
            }
            ID_ADD_LOYALITY_OPTION_ROSSMAN -> {
            //todo: add card
            }
            ID_ADD_LOYALITY_OPTION_PAYBACK -> {
            //todo: add card
            }
        }
    }

    companion object {
        const val ID_NICK_QUESTION = 0
        const val ID_NICK_ANSWER = 1
        const val ID_NICK_FIXED = 2
        const val ID_NICK_PROGRESS = 3
        const val ID_NICK_RETRY_QUESTION = 4

        const val ID_LOGIN_QUESTION = 5
        const val ID_LOGIN_OPTION_GOOGLE = 6
        const val ID_LOGIN_OPTION_FACEBOOK = 7
        const val ID_LOGIN_OPTION_INSTAGRAM = 8
        const val ID_LOGIN_EMAIL = 9
        const val ID_LOGIN_FIXED = 10
        const val ID_LOGIN_PROGRESS = 11

        const val ID_GOOGLE_FIT_QUESTION = 12
        const val ID_GOOGLE_FIT_OPTION = 13
        const val ID_GOOGLE_FIT_ANSWER = 14
        const val ID_SUPER_QUESTION = 15
        const val ID_LOGIN_OPTION_MAIL = 16

        const val ID_LOGIN_PASS = 17
        const val ID_LOGIN_PASS_FIXED = 18

        const val ID_LOGIN_CONDITION_QUESTION = 19
        const val ID_LOGIN_CONDITION_REGULATION = 20
        const val ID_LOGIN_CONDITION_RODO = 21

        const val ID_ADD_LOYALITY_QUESTION = 22
        const val ID_ADD_LOYALITY_OPTION_BIEDRONKA = 23
        const val ID_ADD_LOYALITY_OPTION_ROSSMAN = 24
        const val ID_ADD_LOYALITY_OPTION_PAYBACK = 25
    }

    fun getEmojiByUnicode(unicode: Int): String {
        return String(Character.toChars(unicode))
    }
}