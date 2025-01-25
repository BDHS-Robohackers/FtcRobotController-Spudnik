package org.firstinspires.ftc.teamcode.util;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.AngularVelConstraint;
import com.acmerobotics.roadrunner.MinVelConstraint;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Rotation2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.TranslationalVelConstraint;
import com.acmerobotics.roadrunner.Vector2d;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.arcrobotics.ftclib.command.WaitCommand;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.commands.DriveCommand;
import org.firstinspires.ftc.teamcode.util.LoggingUtils.FTCDashboardPackets;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class AutoTrajectories {

    public static final double WAIT_TIME = 4.605;

    public static class CompAutoTrajectorySequence {
        public final FTCDashboardPackets dbp = new FTCDashboardPackets();
        public static final Pose2d INITIAL_POSE = new Pose2d(new Vector2d(5, -65), Math.toRadians(90));

        private final MecanumDrive DRIVE;
        private final AutonomousActions.Uppie UPPIE;

        public static TrajectoryActionBuilder START;
        public static TrajectoryActionBuilder TO_RUNG;
        public static TrajectoryActionBuilder TO_CORNER;
        public static TrajectoryActionBuilder TO_BLOCK_LEFT;
        public static TrajectoryActionBuilder TO_BLOCK_MIDDLE;
        public static TrajectoryActionBuilder TO_BLOCK_RIGHT;
        public static TrajectoryActionBuilder TO_INTER;

        // Change this to change what the robot does in auto
        private final STATES[] RUN_REEL = {
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
        };

        public enum STATES {
            START,
            BACK_TO_START,
            TO_RUNG(new Pose2d(new Vector2d(5, -39), Math.toRadians(90))),
            TO_RUNG_TWO(new Pose2d(new Vector2d(5, -38), Math.toRadians(90))),
            TO_CORNER(new Pose2d(new Vector2d(64, -67), Math.toRadians(270))),
            BLOCK_LEFT(new Pose2d(new Vector2d(49, -40), Math.toRadians(90))),
            BLOCK_MIDDLE(new Pose2d(new Vector2d(60, -40), Math.toRadians(90))),
            BLOCK_RIGHT(new Pose2d(new Vector2d(70, -40), Math.toRadians(90))),
            PARK_CORNER(new Pose2d(new Vector2d(64, -65), Math.toRadians(270))),
            PARK_SUB,
            GRAB_BLOCK,
            PLACE_RUNG,
            WAIT(),
            DROP_SAMPLE(new Pose2d(new Vector2d(43, -65), Math.toRadians(0))),
            DROP,
            INTERMEDIATE_BLOCK(new Pose2d(new Vector2d(5, -52), Math.toRadians(90))),
            END;

            private final Pose2d END_POSE;
            private final Pose2d START_POSE;

            STATES() {
                END_POSE = INITIAL_POSE;
                START_POSE = INITIAL_POSE;
            }

            STATES(Pose2d endPose) {
                END_POSE = endPose;
                START_POSE = INITIAL_POSE;
            }

            STATES(Pose2d startPose, Pose2d endPose) {
                START_POSE = startPose;
                END_POSE = endPose;
            }

            public Pose2d get_END_POSE() {
                return END_POSE;
            }

            public Pose2d get_START_POSE() {
                return START_POSE;
            }
        }

        public CompAutoTrajectorySequence(MecanumDrive drive, HardwareMap map) {
            DRIVE = drive;
            UPPIE = new AutonomousActions.Uppie(map);
        }

        public SequentialAction build() {
            ArrayList<Action> actions = new ArrayList<>();
            STATES previousState = STATES.START;
            for (STATES state : RUN_REEL) {
                switch (state) {
                    case START:
                        actions.add(UPPIE.toRung());
                        actions.add(generateStartTrajectory().build());
                        break;
                    case BACK_TO_START:
                        actions.add(generateBackToStartTrajectory(previousState).build());
                        break;
                    case WAIT:
                        actions.add(new SleepAction(1.605));
                        break;
                    case TO_RUNG:
                        actions.add(UPPIE.toRung());
                        actions.add(new SleepAction(1));
                        actions.add(generateToRungTrajectory(previousState).build());
                        break;
                    case TO_RUNG_TWO:
                        actions.add(UPPIE.toRung());
                        actions.add(new SleepAction(1));
                        actions.add(generateToRungTwoTrajectory(previousState).build());
                        break;
                    case PLACE_RUNG:
                        actions.add(UPPIE.toAttach());
                        break;
                    case INTERMEDIATE_BLOCK:
                        actions.add(generateToInter(previousState).build());
                        break;
                    case PARK_CORNER:
                        actions.add(generatePark(previousState).build());
                        break;
                    case DROP_SAMPLE:
                    case TO_CORNER:
                        actions.add(UPPIE.toPickup());
                        actions.add(generateToCorner(previousState).build());
                        break;
                    case DROP:
                        actions.add(new SleepAction(1.605));
                        // TODO: Implement Drop action
                        break;
                    case GRAB_BLOCK:
                        actions.add(UPPIE.toPickup());
                        break;
                    case BLOCK_LEFT:
                        actions.add(generateToBlockLeft(previousState).build());
                        break;
                    case BLOCK_MIDDLE:
                        actions.add(generateToBlockMiddle(previousState).build());
                        break;
                    case BLOCK_RIGHT:
                        actions.add(generateToBlockRight(previousState).build());
                        break;
                    case PARK_SUB:
                        // TODO: Implement Park Submersible
                    case END:
                    default:
                        return new SequentialAction(actions);
                }
                previousState = state;
            }
            dbp.info("Sequence: " + actions);
            dbp.send(true);
            return new SequentialAction(actions);
        }

        private TrajectoryActionBuilder generateStartTrajectory() {
            START = DRIVE.actionBuilder(STATES.START.get_START_POSE())
                    .lineToY(STATES.TO_RUNG.END_POSE.position.y);
            return START;
        }

        private TrajectoryActionBuilder generateBackToStartTrajectory(STATES previousState) {
            return DRIVE.actionBuilder(STATES.TO_RUNG.get_END_POSE())
                    .lineToY(STATES.START.get_START_POSE().position.y);
        }

        private TrajectoryActionBuilder generateToRungTrajectory(STATES previousState) {
            TO_RUNG = DRIVE.actionBuilder(previousState.get_END_POSE())
                    .splineTo(STATES.TO_RUNG.END_POSE.position, STATES.TO_RUNG.END_POSE.heading);
            return TO_RUNG;
        }

        private TrajectoryActionBuilder generateToRungTwoTrajectory(STATES previousState) {
            return DRIVE.actionBuilder(previousState.get_END_POSE())
                    .splineTo(STATES.TO_RUNG_TWO.END_POSE.position, STATES.TO_RUNG_TWO.END_POSE.heading);
        }

        private TrajectoryActionBuilder generateToCorner(STATES previousState) {
            Vector2d pos = STATES.TO_CORNER.END_POSE.position;
            Rotation2d heading = STATES.TO_CORNER.END_POSE.heading;
            TO_CORNER = DRIVE.actionBuilder(previousState.get_END_POSE())
                    .splineTo(new Vector2d(pos.x, -50), heading)
                    .waitSeconds(2)
                    .strafeTo(pos, new TranslationalVelConstraint(10));

            return TO_CORNER;
        }

        private TrajectoryActionBuilder generatePark(STATES previousState) {
            Vector2d pos = STATES.PARK_CORNER.END_POSE.position;
            Rotation2d heading = STATES.PARK_CORNER.END_POSE.heading;
            return DRIVE.actionBuilder(previousState.get_END_POSE())
                    .splineTo(pos, heading);
        }

        private TrajectoryActionBuilder generateToInter(STATES previousState) {
            Vector2d pos = STATES.INTERMEDIATE_BLOCK.END_POSE.position;
            Rotation2d heading = STATES.TO_CORNER.END_POSE.heading;
            TO_INTER = DRIVE.actionBuilder(previousState.get_END_POSE())
                    .lineToY(pos.y);

            return TO_INTER;
        }

        private TrajectoryActionBuilder generateToBlockLeft(STATES previousState) {
            Vector2d pos = STATES.BLOCK_LEFT.END_POSE.position;
            Rotation2d heading = STATES.BLOCK_LEFT.END_POSE.heading;
            TO_BLOCK_LEFT = DRIVE.actionBuilder(previousState.get_END_POSE())
                    .splineToConstantHeading(pos, heading);

            return TO_BLOCK_LEFT;
        }

        private TrajectoryActionBuilder generateToBlockMiddle(STATES previousState) {
            Vector2d pos = STATES.BLOCK_MIDDLE.END_POSE.position;
            Rotation2d heading = STATES.BLOCK_MIDDLE.END_POSE.heading;
            TO_BLOCK_MIDDLE = DRIVE.actionBuilder(previousState.get_END_POSE())
                    .splineToConstantHeading(pos, heading);

            return TO_BLOCK_MIDDLE;
        }

        private TrajectoryActionBuilder generateToBlockRight(STATES previousState) {
            Vector2d pos = STATES.BLOCK_RIGHT.END_POSE.position;
            Rotation2d heading = STATES.BLOCK_RIGHT.END_POSE.heading;
            TO_BLOCK_RIGHT = DRIVE.actionBuilder(previousState.get_END_POSE())
                    .splineToConstantHeading(pos, heading);

            return TO_BLOCK_RIGHT;
        }
    }
}
