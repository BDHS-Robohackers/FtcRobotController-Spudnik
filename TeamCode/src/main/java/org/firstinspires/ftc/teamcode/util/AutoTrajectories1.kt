package org.firstinspires.ftc.teamcode.util

import com.acmerobotics.roadrunner.Action
import com.acmerobotics.roadrunner.Pose2d
import com.acmerobotics.roadrunner.SequentialAction
import com.acmerobotics.roadrunner.SleepAction
import com.acmerobotics.roadrunner.TrajectoryActionBuilder
import com.acmerobotics.roadrunner.TranslationalVelConstraint
import com.acmerobotics.roadrunner.Vector2d
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.MecanumDrive
import org.firstinspires.ftc.teamcode.util.AutonomousActions.Uppie
import org.firstinspires.ftc.teamcode.util.LoggingUtils.FTCDashboardPackets

object AutoTrajectories {
    const val WAIT_TIME: Double = 4.605

    class CompAutoTrajectorySequence(private val DRIVE: MecanumDrive, map: HardwareMap) {
        val dbp: FTCDashboardPackets = FTCDashboardPackets()
        private val UPPIE = Uppie(map)

        // Change this to change what the robot does in auto
        private val RUN_REEL = arrayOf(
            STATES.START,
            STATES.PLACE_RUNG,
            STATES.WAIT,
            STATES.BACK_TO_START,
            STATES.TO_CORNER,
            STATES.WAIT,
            STATES.TO_RUNG_TWO,
            STATES.PLACE_RUNG,
            STATES.WAIT,
            STATES.PARK_CORNER,
            STATES.END
        )

        enum class STATES {
            START,
            BACK_TO_START,
            TO_RUNG(Pose2d(Vector2d(5.0, -39.0), Math.toRadians(90.0))),
            TO_RUNG_TWO(Pose2d(Vector2d(5.0, -38.0), Math.toRadians(90.0))),
            TO_CORNER(Pose2d(Vector2d(64.0, -67.0), Math.toRadians(270.0))),
            BLOCK_LEFT(Pose2d(Vector2d(49.0, -40.0), Math.toRadians(90.0))),
            BLOCK_MIDDLE(Pose2d(Vector2d(60.0, -40.0), Math.toRadians(90.0))),
            BLOCK_RIGHT(Pose2d(Vector2d(70.0, -40.0), Math.toRadians(90.0))),
            PARK_CORNER(Pose2d(Vector2d(64.0, -65.0), Math.toRadians(270.0))),
            PARK_SUB,
            GRAB_BLOCK,
            PLACE_RUNG,
            WAIT,
            DROP_SAMPLE(Pose2d(Vector2d(43.0, -65.0), Math.toRadians(0.0))),
            DROP,
            INTERMEDIATE_BLOCK(Pose2d(Vector2d(5.0, -52.0), Math.toRadians(90.0))),
            END;

            val END_POSE: Pose2d
            private val START_POSE: Pose2d

            constructor() {
                END_POSE = INITIAL_POSE
                START_POSE = INITIAL_POSE
            }

            constructor(endPose: Pose2d) {
                END_POSE = endPose
                START_POSE = INITIAL_POSE
            }

            constructor(startPose: Pose2d, endPose: Pose2d) {
                START_POSE = startPose
                END_POSE = endPose
            }

            fun get_END_POSE(): Pose2d {
                return END_POSE
            }

            fun get_START_POSE(): Pose2d {
                return START_POSE
            }
        }

        fun build(): SequentialAction {
            val actions = ArrayList<Action>()
            var previousState = STATES.START
            for (state in RUN_REEL) {
                when (state) {
                    STATES.START -> {
                        actions.add(UPPIE.toRung())
                        actions.add(generateStartTrajectory().build())
                    }

                    STATES.BACK_TO_START -> actions.add(generateBackToStartTrajectory(previousState).build())
                    STATES.WAIT -> actions.add(SleepAction(1.605))
                    STATES.TO_RUNG -> {
                        actions.add(UPPIE.toRung())
                        actions.add(SleepAction(1.0))
                        actions.add(generateToRungTrajectory(previousState).build())
                    }

                    STATES.TO_RUNG_TWO -> {
                        actions.add(UPPIE.toRung())
                        actions.add(SleepAction(1.0))
                        actions.add(generateToRungTwoTrajectory(previousState).build())
                    }

                    STATES.PLACE_RUNG -> actions.add(UPPIE.toAttach())
                    STATES.INTERMEDIATE_BLOCK -> actions.add(generateToInter(previousState).build())
                    STATES.PARK_CORNER -> actions.add(generatePark(previousState).build())
                    STATES.DROP_SAMPLE, STATES.TO_CORNER -> {
                        actions.add(UPPIE.toPickup())
                        actions.add(generateToCorner(previousState).build())
                    }

                    STATES.DROP -> actions.add(SleepAction(1.605))
                    STATES.GRAB_BLOCK -> actions.add(UPPIE.toPickup())
                    STATES.BLOCK_LEFT -> actions.add(generateToBlockLeft(previousState).build())
                    STATES.BLOCK_MIDDLE -> actions.add(generateToBlockMiddle(previousState).build())
                    STATES.BLOCK_RIGHT -> actions.add(generateToBlockRight(previousState).build())
                    STATES.PARK_SUB, STATES.END -> return SequentialAction(actions)
                    else -> return SequentialAction(actions)
                }
                previousState = state
            }
            dbp.info("Sequence: $actions")
            dbp.send(true)
            return SequentialAction(actions)
        }

        private fun generateStartTrajectory(): TrajectoryActionBuilder {
            START = DRIVE.actionBuilder(STATES.START.get_START_POSE())
                .lineToY(STATES.TO_RUNG.END_POSE.position.y)
            return START!!
        }

        private fun generateBackToStartTrajectory(previousState: STATES): TrajectoryActionBuilder {
            return DRIVE.actionBuilder(STATES.TO_RUNG.get_END_POSE())
                .lineToY(STATES.START.get_START_POSE().position.y)
        }

        private fun generateToRungTrajectory(previousState: STATES): TrajectoryActionBuilder {
            TO_RUNG = DRIVE.actionBuilder(previousState.get_END_POSE())
                .splineTo(STATES.TO_RUNG.END_POSE.position, STATES.TO_RUNG.END_POSE.heading)
            return TO_RUNG!!
        }

        private fun generateToRungTwoTrajectory(previousState: STATES): TrajectoryActionBuilder {
            return DRIVE.actionBuilder(previousState.get_END_POSE())
                .splineTo(STATES.TO_RUNG_TWO.END_POSE.position, STATES.TO_RUNG_TWO.END_POSE.heading)
        }

        private fun generateToCorner(previousState: STATES): TrajectoryActionBuilder {
            val pos = STATES.TO_CORNER.END_POSE.position
            val heading = STATES.TO_CORNER.END_POSE.heading
            TO_CORNER = DRIVE.actionBuilder(previousState.get_END_POSE())
                .splineTo(Vector2d(pos.x, -50.0), heading)
                .waitSeconds(2.0)
                .strafeTo(pos, TranslationalVelConstraint(10.0))

            return TO_CORNER!!
        }

        private fun generatePark(previousState: STATES): TrajectoryActionBuilder {
            val pos = STATES.PARK_CORNER.END_POSE.position
            val heading = STATES.PARK_CORNER.END_POSE.heading
            return DRIVE.actionBuilder(previousState.get_END_POSE())
                .splineTo(pos, heading)
        }

        private fun generateToInter(previousState: STATES): TrajectoryActionBuilder {
            val pos = STATES.INTERMEDIATE_BLOCK.END_POSE.position
            val heading = STATES.TO_CORNER.END_POSE.heading
            TO_INTER = DRIVE.actionBuilder(previousState.get_END_POSE())
                .lineToY(pos.y)

            return TO_INTER!!
        }

        private fun generateToBlockLeft(previousState: STATES): TrajectoryActionBuilder {
            val pos = STATES.BLOCK_LEFT.END_POSE.position
            val heading = STATES.BLOCK_LEFT.END_POSE.heading
            TO_BLOCK_LEFT = DRIVE.actionBuilder(previousState.get_END_POSE())
                .splineToConstantHeading(pos, heading)

            return TO_BLOCK_LEFT!!
        }

        private fun generateToBlockMiddle(previousState: STATES): TrajectoryActionBuilder {
            val pos = STATES.BLOCK_MIDDLE.END_POSE.position
            val heading = STATES.BLOCK_MIDDLE.END_POSE.heading
            TO_BLOCK_MIDDLE = DRIVE.actionBuilder(previousState.get_END_POSE())
                .splineToConstantHeading(pos, heading)

            return TO_BLOCK_MIDDLE!!
        }

        private fun generateToBlockRight(previousState: STATES): TrajectoryActionBuilder {
            val pos = STATES.BLOCK_RIGHT.END_POSE.position
            val heading = STATES.BLOCK_RIGHT.END_POSE.heading
            TO_BLOCK_RIGHT = DRIVE.actionBuilder(previousState.get_END_POSE())
                .splineToConstantHeading(pos, heading)

            return TO_BLOCK_RIGHT!!
        }

        companion object {
            val INITIAL_POSE: Pose2d = Pose2d(Vector2d(5.0, -65.0), Math.toRadians(90.0))

            var START: TrajectoryActionBuilder? = null
            var TO_RUNG: TrajectoryActionBuilder? = null
            var TO_CORNER: TrajectoryActionBuilder? = null
            var TO_BLOCK_LEFT: TrajectoryActionBuilder? = null
            var TO_BLOCK_MIDDLE: TrajectoryActionBuilder? = null
            var TO_BLOCK_RIGHT: TrajectoryActionBuilder? = null
            var TO_INTER: TrajectoryActionBuilder? = null
        }
    }
}
