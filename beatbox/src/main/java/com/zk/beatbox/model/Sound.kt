package com.zk.beatbox.model



private const val WAV = ".wav"

/**
 * @author ZhuKun
 * @date 2021/12/21
 * @see 1、Sound的模型
 */
class Sound(val assetPath: String, var soundId: Int? = null) {
    val name = assetPath.split("/").last().removeSuffix(WAV)
}