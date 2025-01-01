package org.firstinspires.ftc.teamcode.util;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Rotation2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.arcrobotics.ftclib.command.WaitCommand;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.commands.DriveCommand;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AutoTrajectories {

    public static final double WAIT_TIME = 4.605;

    public static class CompAutoTrajectorySequence {
        public static final Pose2d INITIAL_POSE = new Pose2d(new Vector2d(5, -65), Math.toRadians(90));

        private final MecanumDrive DRIVE;
        private final AutonomousActions.Uppie UPPIE;

        public static TrajectoryActionBuilder START;
        public static TrajectoryActionBuilder TO_RUNG;
        public static TrajectoryActionBuilder TO_CORNER;
        public static TrajectoryActionBuilder TO_BLOCK_LEFT;
        public static TrajectoryActionBuilder TO_BLOCK_MIDDLE;
        public static TrajectoryActionBuilder TO_BLOCK_RIGHT;

        // Change this to change what the robot does in auto
        private final STATES[] RUN_REEL = {
                STATES.START,
                STATES.PLACE_RUNG,
                STATES.BLOCK_LEFT,
                STATES.WAIT,
                STATES.DROP_SAMPLE,
                STATES.DROP,
                STATES.WAIT,
                STATES.TO_CORNER,
                STATES.WAIT,
                STATES.TO_RUNG,
                STATES.PLACE_RUNG,
                STATES.BLOCK_MIDDLE,
                STATES.DROP_SAMPLE,
                STATES.WAIT,
                STATES.DROP,
                STATES.END
        };

        public enum STATES {
            START,
            TO_RUNG(new Pose2d(new Vector2d(5, -35), Math.toRadians(90))),
            TO_CORNER(new Pose2d(new Vector2d(64, -65), Math.toRadians(0))),
            BLOCK_LEFT(new Pose2d(new Vector2d(49, -35), Math.toRadians(90))),
            BLOCK_MIDDLE(new Pose2d(new Vector2d(60, -35), Math.toRadians(90))),
            BLOCK_RIGHT(new Pose2d(new Vector2d(70, -35), Math.toRadians(90))),
            PARK_CORNER(new Pose2d(new Vector2d(43, -65), Math.toRadians(90))),
            PARK_SUB,
            GRAB_BLOCK,
            PLACE_RUNG,
            WAIT(),
            DROP_SAMPLE(new Pose2d(new Vector2d(43, -65), Math.toRadians(0))),
            DROP,
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
                        ParallelAction startAction = new ParallelAction(
                                generateStartTrajectory().build(),
                                UPPIE.toRung()
                        );
                        actions.add(startAction);
                        break;
                    case WAIT:
                        actions.add(new SleepAction(1.605));
                        break;
                    case TO_RUNG:
                        ParallelAction toRungAction = new ParallelAction(
                                generateToRungTrajectory(previousState).build(),
                                UPPIE.toRung()
                        );
                        actions.add(toRungAction);
                        break;
                    case PLACE_RUNG:
                        actions.add(UPPIE.toAttach());
                        break;
                    case PARK_CORNER:
                    case DROP_SAMPLE:
                    case TO_CORNER:
                        actions.add(generateToCorner(previousState).build());
                        break;
                    case DROP:
                        actions.add(new SleepAction(1.605));
                        // TODO: Implement Drop action
                        break;
                    case GRAB_BLOCK:
                        actions.add(new SleepAction(1.605));
                        // TODO: Implement grab block action
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
            return new SequentialAction(actions);
        }

        private TrajectoryActionBuilder generateStartTrajectory() {
            START = DRIVE.actionBuilder(STATES.START.get_START_POSE())
                    .lineToX(STATES.TO_RUNG.END_POSE.position.x);
            return START;
        }

        private TrajectoryActionBuilder generateToRungTrajectory(STATES previousState) {
            TO_RUNG = DRIVE.actionBuilder(previousState.get_END_POSE())
                    .splineToConstantHeading(STATES.TO_RUNG.END_POSE.position, STATES.TO_RUNG.END_POSE.heading);
            return TO_RUNG;
        }

        private TrajectoryActionBuilder generateToCorner(STATES previousState) {
            Vector2d pos = STATES.TO_CORNER.END_POSE.position;
            Rotation2d heading = STATES.TO_CORNER.END_POSE.heading;
            TO_CORNER = DRIVE.actionBuilder(previousState.get_END_POSE())
                    .splineToConstantHeading(new Vector2d(40, pos.y), heading)
                    .waitSeconds(2)
                    .splineToConstantHeading(pos, heading);

            return TO_CORNER;
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
