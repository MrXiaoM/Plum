package com.sakurawald.plum.reloaded.command.commands

import com.sakurawald.plum.reloaded.command.RobotCommand
import com.sakurawald.plum.reloaded.command.RobotCommandChatType
import com.sakurawald.plum.reloaded.command.RobotCommandUser
import com.sakurawald.plum.reloaded.config.PlumConfig
import com.sakurawald.plum.reloaded.timer.timers.TimerDailyPoetry
import com.sakurawald.plum.reloaded.utils.checkLengthAndModifySendMsg
import com.sakurawald.plum.reloaded.utils.sendMessageBySituation
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.message.data.MessageChain
import utils.DateUtil

object DailyPoetryExplanationCommand : RobotCommand(
    "#解读.*",
    mutableListOf(
        RobotCommandChatType.FRIEND_CHAT,
        RobotCommandChatType.GROUP_TEMP_CHAT,
        RobotCommandChatType.GROUP_CHAT,
        RobotCommandChatType.STRANGER_CHAT
    ),
    mutableListOf(
        RobotCommandUser.NORMAL_USER,
        RobotCommandUser.GROUP_ADMINISTRATOR,
        RobotCommandUser.GROUP_OWNER,
        RobotCommandUser.BOT_ADMINISTRATOR
    )
) {
    override suspend fun runCommand(
        msgType: Int,
        time: Int,
        fromGroup: Group?,
        fromQQ: User,
        messageChain: MessageChain
    ) {
        if (!PlumConfig.functions.dailyPoetry.explanationEnable) {
            sendMessageBySituation(
                fromGroup,
                fromQQ,
                PlumConfig.functions.functionManager.functionDisableMsg
            )
            return
        }

        val targetPoetry = TimerDailyPoetry.todayPoetry
        if (targetPoetry == null) {
            sendMessageBySituation(
                fromGroup, fromQQ,
                "很抱歉，目前暂时没有任何诗词可以解读，请稍后再试吧."
            )
            return
        }

        var sendMsg = """
            诗词解读，${DateUtil.nowYear}年${DateUtil.nowMonth}月${DateUtil.nowDay}日！
            
            ●今日诗词
            〖标题〗${targetPoetry.title}
            〖作者〗（${targetPoetry.dynasty}） ${targetPoetry.author}
            〖作者简介〗
            ${targetPoetry.authorIntroduction}
            〖译文〗
            ${targetPoetry.translation}
            〖注释〗
            ${targetPoetry.note}
            
            """.trimIndent().trim { it <= ' ' }

        /** 字数检测 **/
        val defaultMsg = """
            诗词解读，${DateUtil.nowYear}年${DateUtil.nowMonth}月${DateUtil.nowDay}日！
            
            ●今日诗词
            〖链接〗由于本次诗词解读文本过长，请直接点击链接查看：
            https://hanyu.baidu.com/
            """.trimIndent()

        sendMsg = checkLengthAndModifySendMsg(sendMsg) { defaultMsg }

        /** 发送sendMsg **/
        sendMessageBySituation(fromGroup, fromQQ, sendMsg)
    }
}