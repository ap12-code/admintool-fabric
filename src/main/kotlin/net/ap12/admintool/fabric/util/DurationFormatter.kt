package net.ap12.admintool.fabric.util

import net.ap12.admintool.fabric.i18n.t
import net.ap12.admintool.fabric.i18n.translate
import net.ap12.admintool.fabric.util.components.join
import net.minecraft.network.chat.Component
import java.util.*
import kotlin.time.Duration

class DurationFormatter {
    fun format(duration: Duration, locale: Locale = Locale.US) =
        duration.toComponents { days, hours, minutes, seconds, _ ->
            listOfNotNull(
                    if (days > 0)
                        t("admintool.duration.days", Component.literal(days.toString()))
                            .translate(locale)
                    else null,
                    if (hours > 0)
                        t("admintool.duration.hours", Component.literal(hours.toString()))
                            .translate(locale)
                    else null,
                    if (minutes > 0)
                        t("admintool.duration.minutes", Component.literal(minutes.toString()))
                            .translate(locale)
                    else null,
                    if (seconds > 0)
                        t("admintool.duration.seconds", Component.literal(seconds.toString()))
                            .translate(locale)
                    else null,
                )
                .join(" ")
        }
}

fun Duration.format(formatter: DurationFormatter, locale: Locale = Locale.US): Component =
    formatter.format(this, locale)
