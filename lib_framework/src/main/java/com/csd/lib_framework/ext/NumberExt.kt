package com.csd.lib_framework.ext

import com.csd.lib_framework.util.SizeUtils

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/03
 */

val Number.dp: Int
    get() = (SizeUtils.dp2px(this.toFloat()) + 0.5f).toInt()

val Number.sp: Int
    get() = SizeUtils.sp2px(this.toFloat() + 0.5f).toInt()

val Number.dpf: Float
    get() = SizeUtils.dp2px(this.toFloat())

val Number.spf: Float
    get() = SizeUtils.sp2px(this.toFloat())