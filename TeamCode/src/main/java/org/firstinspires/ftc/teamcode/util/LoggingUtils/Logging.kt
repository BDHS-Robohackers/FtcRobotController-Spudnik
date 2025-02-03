// Logging utility class. Sets a custom logging handler to write log messages to a file on the
// controller phone "disk". You can then pull the file back to your PC with an adb command in the
// Android Studio terminal window.
//
// You normally only need to call the logger.setup function in your class before any logging.
// You can use LogPrintStream to send streams to the log file.
//
// Use the logger object in your code to write log messages or use the simpler methods
// in this class. The methods in this class record the location in your program where
// you call them.
package org.firstinspires.ftc.teamcode.util.LoggingUtils

import com.qualcomm.robotcore.hardware.HardwareDevice
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.HardwareMap.DeviceMapping
import java.io.IOException
import java.io.OutputStream
import java.io.PrintStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import java.util.logging.ConsoleHandler
import java.util.logging.FileHandler
import java.util.logging.Formatter
import java.util.logging.Level
import java.util.logging.LogRecord
import java.util.logging.Logger

/**
 * Custom logging class. Configures log system (not console) to write to a disk file.
 * Customize the setup() method for the controller phone you are using.
 */
object Logging {
    /**
     * PrintStream that writes to out custom logging location.
     */
    val logPrintStream: PrintStream = PrintStream(LoggingOutputStream())

    /**
     * Used by other classes to implement logging. Other classes should log with methods on this
     * object or the simpler methods included in this class. The methods in class record the
     * location in your program where you call them.
     */
    val logger: Logger = Logger.getGlobal()

    // The log file can be copied from the ZTE robot controller to your PC for review by using the
    // following AndroidDeBugger command in the Android Studio Terminal window:
    //
    // ZTE: adb pull //storage/sdcard0/Logging.txt c:\temp\robot_logging.txt
    // MOTO G: adb pull sdcard/Logging.txt c:\temp\robot_logging.txt
    // Control Hub: adb pull sdcard/Logging.txt c:\temp\robot_Logging.txt
    /**
     * Indicates if logging is turned on or off.
     */
    var enabled: Boolean = true

    private var fileTxt: FileHandler? = null

    //static private SimpleFormatter	formatterTxt;
    private var logFormatter: LogFormatter? = null
    private var isSetup = false

    /**
     * Configures our custom logging. If you don't use this custom logging, logging will go to
     * the default logging location, typically the console. Call setup to turn on custom logging.
     * With custom logging turned on logging goes to the console and also to the file opened in
     * setup().
     */
    /**
     * Call to initialize our custom logging system.
     * @throws IOException
     */
    @Throws(IOException::class)
    fun setup() {
        if (isSetup) {
            logger.info("========================================================================")
            return
        }

        // get the global logger to configure it and add a file handler.
        val logger = Logger.getGlobal()

        logger.level = Level.ALL

        // If we decide to redirect system.out to our log handler, then following
        // code will delete the default log handler for the console to prevent
        // a recursive loop. We would only redirect system.out if we only want to
        // log to the file. If we delete the console handler we can skip setting
        // the formatter...otherwise we set our formatter on the console logger.
        val rootLogger = Logger.getLogger("")

        val handlers = rootLogger.handlers

        //            if (handlers[0] instanceof ConsoleHandler)
//            {
//                rootLogger.removeHandler(handlers[0]);
//                return;
//            }
        logFormatter = LogFormatter()

        // Set our formatter on the console log handler.
        if (handlers[0] is ConsoleHandler) handlers[0].formatter =
            logFormatter

        // Now create a handler to log to a file on controller phone "disk".

        // For ZTE:
        //fileTxt = new FileHandler("storage/sdcard0/Logging.txt", 0 , 1);

        // For MOTO G:
        fileTxt = FileHandler("sdcard/Logging.txt", 0, 1)

        fileTxt!!.formatter =
            logFormatter

        logger.addHandler(fileTxt)

        isSetup = true
    }

    /**
     * Flush logged data to disk file. Not normally needed.
     */
    fun flushlog() {
        fileTxt!!.flush()
    }

    /**
     * Log blank line with program location.
     */
    fun log() {
        if (!enabled) return

        logger.log(Level.INFO, currentMethod(2))
    }

    /**
     * Log message with optional formatting and program location.
     * @param message message with optional format specifiers for listed parameters
     * @param parms parameter list matching format specifiers
     */
    fun log(message: String, vararg parms: Any?) {
        if (!enabled) return

        logger.log(
            Level.INFO,
            String.format("%s: %s", currentMethod(2), String.format(message, *parms))
        )
    }

    /**
     * Log message with optional formatting and no program location.
     * @param message message with optional format specifiers for listed parameters
     * @param parms parameter list matching format specifiers
     */
    fun logNoMethod(message: String, vararg parms: Any?) {
        if (!enabled) return

        logger.log(Level.INFO, String.format(message, *parms))
    }

    /**
     * Log message with no formatting and program location.
     * @param message message with optional format specifiers for listed parameters
     */
    fun logNoFormat(message: String?) {
        if (!enabled) return

        logger.log(Level.INFO, String.format("%s: %s", currentMethod(2), message))
    }

    /**
     * Log message with no formatting and no program location.
     * @param message message with optional format specifiers for listed parameters
     */
    fun logNoFormatNoMethod(message: String?) {
        if (!enabled) return

        logger.log(Level.INFO, message)
    }

    /**
     * Returns program location where call to this method is located.
     */
    fun currentMethod(): String {
        return currentMethod(2)
    }

    private fun currentMethod(level: Int): String {
        val stackTrace = Throwable().stackTrace

        try {
            return stackTrace[level].toString().split("teamcode.".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()[1]
        } catch (e: Exception) {
            try {
                return stackTrace[level].toString().split("lib.".toRegex())
                    .dropLastWhile { it.isEmpty() }
                    .toTypedArray()[1]
            } catch (e1: Exception) {
                try {
                    return stackTrace[level].toString().split("activities.".toRegex())
                        .dropLastWhile { it.isEmpty() }
                        .toTypedArray()[1]
                } catch (e2: Exception) {
                    e2.printStackTrace()
                    return ""
                }
            }
        }
    }

    /**
     * Write a list of configured hardware devices to the log file. Can be called in init()
     * function or later.
     * @param map hardwareMap object.
     */
    fun logHardwareDevices(map: HardwareMap) {
        log()

        // This list must be manually updated when First releases support for new devices.
        logDevices(map.dcMotorController)
        logDevices(map.dcMotor)
        logDevices(map.servoController)
        logDevices(map.servo)
        logDevices(map.analogInput)
        logDevices(map.digitalChannel)
        logDevices(map.pwmOutput)
        logDevices(map.accelerationSensor)
        logDevices(map.colorSensor)
        logDevices(map.compassSensor)
        logDevices(map.gyroSensor)
        logDevices(map.irSeekerSensor)
        logDevices(map.i2cDevice)
        logDevices(map.led)
        logDevices(map.lightSensor)
        logDevices(map.opticalDistanceSensor)
        logDevices(map.touchSensor)
        logDevices(map.ultrasonicSensor)
    }

    private fun logDevices(deviceMap: DeviceMapping<*>) {
        for ((key, device) in deviceMap.entrySet() as Set<Map.Entry<String, HardwareDevice>>) {
            log(
                "%s;%s;%s",
                key, device.deviceName, device.connectionInfo
            )
        }
    }

    /**
     * Get the user assigned name for a hardware device.
     * @param deviceMap The DEVICE_TYPE map, such as hardwareDevice.dcMotor, that the dev belongs to.
     * @param dev Instance of a device of DEVICE_TYPE.
     * @return User assigned name or empty string if not found.
     */
    fun getDeviceUserName(deviceMap: DeviceMapping<*>, dev: HardwareDevice): String {
        for ((key, device) in deviceMap.entrySet() as Set<Map.Entry<String, HardwareDevice>>) {
            if (dev === device) return key
        }

        return ""
    }

    // Our custom formatter for logging output.
    private class LogFormatter : Formatter() {
        override fun format(rec: LogRecord): String {
            val buf = StringBuffer(1024)

            buf.append(String.format("<%d>", rec.threadID)) //Thread.currentThread().getId()));
            buf.append(formatDate(rec.millis))
            buf.append(" ")
            buf.append(formatMessage(rec))
            buf.append("\r\n")

            return buf.toString()
        }

        fun formatDate(milliseconds: Long): String {
            val dateFormat = SimpleDateFormat("hh:mm:ss:SSS")
            dateFormat.timeZone = TimeZone.getTimeZone("America/Los_Angeles")
            val resultDate = Date(milliseconds)
            return dateFormat.format(resultDate)
        }
    }

    // An output stream that writes to our logging system. Writes data with flush on
    // flush call or on a newline character in the stream.
    private class LoggingOutputStream : OutputStream() {
        private var hasBeenClosed = false
        private var buf: ByteArray
        private var count = 0
        private var curBufLength: Int

        init {
            curBufLength = DEFAULT_BUFFER_LENGTH
            buf = ByteArray(curBufLength)
        }

        @Throws(IOException::class)
        override fun write(b: Int) {
            if (!enabled) return

            if (hasBeenClosed) {
                throw IOException("The stream has been closed.")
            }

            // don't log nulls
            if (b == 0) return

            // force flush on newline character, dropping the newline.
            if (b.toByte() == '\n'.code.toByte()) {
                flush()
                return
            }

            // would this be writing past the buffer?
            if (count == curBufLength) {
                // grow the buffer
                val newBufLength = curBufLength + DEFAULT_BUFFER_LENGTH
                val newBuf = ByteArray(newBufLength)
                System.arraycopy(buf, 0, newBuf, 0, curBufLength)
                buf = newBuf
                curBufLength = newBufLength
            }

            buf[count] = b.toByte()

            count++
        }

        override fun flush() {
            if (count == 0) return

            val bytes = ByteArray(count)

            System.arraycopy(buf, 0, bytes, 0, count)

            val str = String(bytes)

            logNoFormatNoMethod(str)

            count = 0
        }

        override fun close() {
            flush()

            hasBeenClosed = true
        }

        companion object {
            private const val DEFAULT_BUFFER_LENGTH = 2048
        }
    }
}