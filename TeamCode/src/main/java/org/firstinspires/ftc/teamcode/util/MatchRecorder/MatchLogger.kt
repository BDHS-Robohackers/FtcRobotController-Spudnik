package org.firstinspires.ftc.teamcode.util.MatchRecorder

import android.os.Environment
import com.acmerobotics.roadrunner.Pose2d
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter
import java.text.DecimalFormat
import java.util.Objects
import kotlin.math.max

class MatchLogger {
    var matchNumber: Int = 0
    private var latestPose: Pose2d? = null

    enum class FileType(var filename: String) {
        VERBOSE("verbose.txt"),
        SCORE("score_prediction.txt"),
        POSITION("position.txt"),
        ARM("arm.txt"),
        FINGER("finger.txt"),
        WRIST("wrist.txt"),
        LAUNCHER("launcher.txt"),
    }

    init {
        try {
            val dataFolder = dataFolder
            dataFolder!!.mkdirs()

            var greatestMatchNumber = 1
            val listFiles = dataFolder.listFiles()
            if (listFiles != null) {
                for (i in listFiles.indices) {
                    val file = listFiles[i]
                    if (!file.isDirectory) {
                        continue
                    }
                    val name = file.name
                    if (!name.contains(MATCH_FILE_NAME)) {
                        // Not a valid match directory
                        continue
                    }
                    val matchNumber = name.substring(MATCH_FILE_NAME.length, name.length - 1)
                    // TODO: Verify that the substring thing works and doesn't produce an error
                    try {
                        val parsedInteger = matchNumber.toInt()
                        greatestMatchNumber =
                            max(parsedInteger.toDouble(), greatestMatchNumber.toDouble())
                                .toInt()
                    } catch (e: NumberFormatException) {
                        e.printStackTrace()
                    }
                }
            }

            matchNumber = greatestMatchNumber
            matchFolder!!.mkdirs()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val dataFolder: File?
        /**
         * @return file object representing the folder where all the match directories will be located
         */
        get() {
            try {
                return File(
                    String.format(
                        "%s/FIRST/data/",
                        Environment.getExternalStorageDirectory().absolutePath
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }

    private fun getMatchFolder(matchNumber: Int): File? {
        try {
            return File(dataFolder.toString() + "/match_" + matchNumber + "/")
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    val matchFolder: File?
        get() = getMatchFolder(matchNumber)

    fun write(message: String?, vararg fileTypes: FileType?) {
        // TODO: Append to the specified files
        try {
            for (i in 0 until fileTypes.size + 1) {
                var fileType: FileType? = null

                // Always write to verbose file
                fileType = if (i == fileTypes.size) {
                    FileType.VERBOSE
                } else {
                    fileTypes[i]
                }

                val textFilePath = File(matchFolder, fileType!!.filename)
                try {
                    val inputStream = FileOutputStream(textFilePath, true)
                    val printWriter = PrintWriter(inputStream)
                    printWriter.println(message)
                    printWriter.close()
                    inputStream.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        println(message)
    }

    /**
     * Records the current robot position
     */
    // TODO: there is no framework for logging positions in the DriveSubsystem for normal driving (no odometry)
    fun logRobotPose(pose2d: Pose2d) {
        val format = DecimalFormat("#.##")
        val position = pose2d.position
        val rotation2d = pose2d.heading
        val message = String.format(
            "[%s, \t%s], \t[%s, \t%s]",
            format.format(position.x), format.format(position.y),
            format.format(rotation2d.real), format.format(rotation2d.imag)
        )
        write(message, FileType.POSITION, FileType.VERBOSE)

        // Record the latest pose for teleop when auto is finished.
        latestPose = pose2d
    }

    private fun arrayToString(relevantVariables: Array<out Any>): String {
        if (relevantVariables == null) {
            return ""
        }
        if (relevantVariables.size == 0) {
            return ""
        }
        var message = ""
        for (i in relevantVariables.indices) {
            message += Objects.toString(relevantVariables[i])
            if (i != relevantVariables.size - 1) {
                message += " "
            }
        }
        return message
    }

    /**
     * Records the passed variables and logs the method that was called before this.
     */
    /*public void logArm(ArmSubsystem subsystem, Object...relevantVariables) {
        String message = String.format("Arm: %s | %s", getCalledMethodName(), arrayToString(relevantVariables));
        write(message, FileType.ARM, FileType.VERBOSE);
    }*/
    fun genericLog(header: String?, fileType: FileType?, vararg relevantVariables: Any) {
        try {
            val message =
                String.format("%s: %s", calledMethodName, arrayToString(relevantVariables))
            write(message, fileType, FileType.VERBOSE)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val calledMethodName: String
        get() {
            try {
                if (REFLECTIONLESS) {
                    return "[reflectionless]"
                }
                val elements = Throwable().stackTrace
                if (elements == null || elements.size == 0) {
                    return "[err_no_stacktrace]"
                }
                for (i in elements.indices) {
                    val calledClassName = elements[i].className
                    if (calledClassName == javaClass.name) {
                        continue
                    }
                    // It is something other than the MatchLogger, record the method used
                    return String.format("[%s]", elements[i].methodName)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return "[err_404]" // not found
        }

    companion object {
        private const val MATCH_FILE_NAME = "match_"
        var matchLogger: MatchLogger? = null
        const val REFLECTIONLESS: Boolean = false // turn to true if the robot is slow

        val instance: MatchLogger?
            get() {
                if (matchLogger == null) {
                    matchLogger = MatchLogger()
                }
                return matchLogger
            }

        fun youJustLostTheGame(lol: Any?): String {
            return "Lol"
        }
    }
}
