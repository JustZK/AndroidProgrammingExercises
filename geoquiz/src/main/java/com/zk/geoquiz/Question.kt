package com.zk.geoquiz

import androidx.annotation.StringRes

/**
 * @author ZhuKun
 * @date 2021/12/30
 * @see
 */
data class Question(@StringRes val testResId: Int, val answer: Boolean) {
}