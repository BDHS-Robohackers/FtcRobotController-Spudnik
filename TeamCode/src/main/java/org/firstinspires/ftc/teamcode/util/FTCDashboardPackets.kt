package org.firstinspires.ftc.teamcode.util

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import java.io.IOException
import java.util.Locale

class FTCDashboardPackets {
    /**
     * The key to be used when putting a value into a packet.
     * Should be set as the name of the class that is using this.
     */
    private val CONTEXT: String
    private var packet: TelemetryPacket? = null
    private val USE_LOGGING: Boolean

    enum class LoggingLevels {
        INFO,
        DEBUG {
            override fun get_level(): String {
                return "DEBUG"
            }
        },
        ERROR {
            override fun get_level(): String {
                return "ERROR"
            }
        },
        WARN {
            override fun get_level(): String {
                return "WARN"
            }
        };

        open fun get_level(): String {
            return "INFO"
        }
    }

    constructor() {
        CONTEXT = "ROOT"
        USE_LOGGING = true
        createNewTelePacket()
    }

    constructor(context: String) {
        CONTEXT = context.uppercase(Locale.getDefault())
        USE_LOGGING = true
        createNewTelePacket()
    }

    constructor(_useLogging: Boolean) {
        CONTEXT = "ROOT"
        USE_LOGGING = _useLogging
        createNewTelePacket()
    }

    /**
     * Creates a new telemetry packet,
     * or initializes one if it does
     * not exist.
     */
    fun createNewTelePacket() {
        packet = TelemetryPacket()
        if (USE_LOGGING) {
            try {
                Logging.setup()
            } catch (e: IOException) {
                error(e, true, false)
            }
        }
    }

    private fun getLoggingLevel(level: LoggingLevels): String {
        return level.get_level()
    }

    private fun log(value: String) {
        if (USE_LOGGING) Logging.log("%s:%s\t%s", CONTEXT, LoggingLevels.INFO.get_level(), value)
    }

    private fun log(value: String?, level: LoggingLevels) {
        if (USE_LOGGING) Logging.log("%s:%s\t%s", CONTEXT, getLoggingLevel(level), value)
    }

    private fun log(key: String, value: String) {
        if (USE_LOGGING) Logging.log(
            "%s:%s\t%s : %s",
            CONTEXT,
            LoggingLevels.INFO.get_level(),
            key,
            value
        )
    }

    private fun log(key: String, value: String, level: LoggingLevels) {
        if (USE_LOGGING) Logging.log("%s:%s\t%s : %s", CONTEXT, getLoggingLevel(level), key, value)
    }

    /**
     * Takes in a key and a value, and puts them both into the current packet.
     * @param key The string to be put into the keys of a packet
     * @param value The string to be put into the values of a packet
     */
    fun put(key: String, value: String) {
        packet!!.put(key, value)
        log(key, value)
    }

    /**
     * Takes in a value and puts it into the current packet.
     * @param value The string to be put into the values of a packet
     */
    fun put(value: String) {
        packet!!.put(CONTEXT, value)
        log(value)
    }

    /**
     * Takes in an exception and puts it into the current packet.
     * @param e The exception to be put into the packet
     */
    fun error(e: Exception) {
        packet!!.put("Error", e.message)
        log(e.message, LoggingLevels.ERROR)
    }

    /**
     * Takes in an exception and puts it into the current packet.
     * @param message The message to be put into the packet
     */
    fun error(message: String?) {
        packet!!.put("Error", message)
        log(message, LoggingLevels.ERROR)
    }

    fun error(message: String?, sendPacket: Boolean) {
        error(message)
        if (sendPacket) send(false)
    }

    /**
     * Takes in an exception and puts it into the current packet.
     * @param e The exception to be put into the packet
     * @param sendPacket A boolean of whether or not to send the packet after the exception is put in
     * @param reinitializePacket A boolean of whether or not to reinitialize the packet after it is sent
     */
    fun error(e: Exception, sendPacket: Boolean, reinitializePacket: Boolean) {
        error(e)
        if (sendPacket) send(reinitializePacket)
    }

    /**
     * Takes in a message and puts it into the current packet.
     * @param message The message to be put into the packet
     */
    fun warn(message: String) {
        packet!!.put("$CONTEXT : WARN", message)
        log(message)
    }

    /**
     * Takes in a message and puts it into the current packet.
     * @param message The message to be put into the packet
     * @param sendPacket Whether or not to send the packet
     */
    fun warn(message: String, sendPacket: Boolean) {
        warn(message)
        if (sendPacket) send(false)
    }

    /**
     * Takes in a message and puts it into the current packet.
     * @param message The message to be put into the packet
     */
    fun info(message: String) {
        packet!!.put("$CONTEXT : INFO", message)
        log(message)
    }

    /**
     * Takes in a message and puts it into the current packet.
     * @param message The message to be put into the packet
     * @param sendPacket A boolean of whether or not to send the packet after the message is put in
     */
    fun info(message: String, sendPacket: Boolean) {
        info(message)
        if (sendPacket) send(false)
    }

    /**
     * Takes in a message and puts it into the current packet.
     * @param message The message to be put into the packet
     * @param sendPacket A boolean of whether or not to send the packet after the message is put in
     * @param reinitializePacket A boolean of whether or not to reinitialize the packet after it is sent
     */
    fun info(message: String, sendPacket: Boolean, reinitializePacket: Boolean) {
        info(message)
        if (sendPacket) send(reinitializePacket)
    }

    /**
     * Takes in a message and puts it into the current packet.
     * @param message The message to be put into the packet
     */
    fun debug(message: String?) {
        packet!!.put("$CONTEXT : DEBUG", message)
        log(message, LoggingLevels.DEBUG)
    }

    /**
     * Takes in a message and puts it into the current packet, sends it,
     * but does not reinitialize the packet.
     * @param message The message to be put into the packet
     * @param sendPacket A boolean of whether or not to send the packet after the message is put in
     */
    fun debug(message: String?, sendPacket: Boolean) {
        debug(message)
        if (sendPacket) send(false)
    }

    /**
     * Takes in a message and puts it into the current packet.
     * @param message The message to be put into the packet
     * @param sendPacket A boolean of whether or not to send the packet after the message is put in
     * @param reinitializePacket A boolean of whether or not to reinitialize the packet after it is sent
     */
    fun debug(message: String?, sendPacket: Boolean, reinitializePacket: Boolean) {
        debug(message)
        if (sendPacket) send(reinitializePacket)
    }

    /**
     * Sends the current packet to the dashboard
     * @param reinitializePacket A boolean of whether or not to reinitialize the packet after it is sent.
     */
    fun send(reinitializePacket: Boolean) {
        DASHBOARD.sendTelemetryPacket(packet)
        if (reinitializePacket) createNewTelePacket()
    }

    companion object {
        val DASHBOARD: FtcDashboard = FtcDashboard.getInstance()
    }
}