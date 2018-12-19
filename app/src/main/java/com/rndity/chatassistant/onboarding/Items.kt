/*
 * Created and developed by RnDity sp. z o.o. in 2018.
 * Copyright © 2018 RnDity sp. z o.o. All rights reserved.
 */

package com.rndity.chatassistant.onboarding

import com.rndity.chatassistant.Item
import com.rndity.chatassistant.ItemId
import java.io.Serializable

//TODO: how to force immatability for history? clone?
class QuestionHeaderItem(id: ItemId, val text: String): Item(id)
class QuestionTextItem(id: ItemId, val text: String) : Item(id)
class TextEnterItem(id: ItemId, var text: String? = null, var hint: String? = null) : Item(id)
class TextFixedItem(id: ItemId, val text: String) : Item(id)
class ProgressItem(id: ItemId) : Item(id)
class RadioOption(val id: Int, val text: String) : Serializable
class RadioOptionsItem(id: ItemId, val question: String, val options: List<RadioOption>) : Item(id)
