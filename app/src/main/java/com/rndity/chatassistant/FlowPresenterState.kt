/*
 * Created and developed by RnDity sp. z o.o. in 2018.
 * Copyright © 2018 RnDity sp. z o.o. All rights reserved.
 */

package com.rndity.chatassistant

import java.io.Serializable

data class FlowPresenterState(val pages: List<ItemsContext>, val activePage: Int) : Serializable
