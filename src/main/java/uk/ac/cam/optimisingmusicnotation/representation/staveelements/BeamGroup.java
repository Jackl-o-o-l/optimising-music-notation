package uk.ac.cam.optimisingmusicnotation.representation.staveelements;

import uk.ac.cam.optimisingmusicnotation.rendering.MusicCanvas;
import uk.ac.cam.optimisingmusicnotation.representation.properties.ChordAnchors;
import uk.ac.cam.optimisingmusicnotation.representation.properties.RenderingConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BeamGroup implements StaveElement {
    private static class Beam {
        int startIndex;
        int endIndex;
        // Zero indexed beams
        int number;

        public Beam(int startIndex, int endIndex, int number) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.number = number;
        }
    }

    private final List<Chord> chords;
    // The list of secondary beams. Note that beam groups always have an implicit first level beam.
    private final List<Beam> beams;

    public BeamGroup() {
        chords = new ArrayList<>();
        beams = new ArrayList<>();
    }

    public BeamGroup(List<Chord> chords) {
        this.chords = chords;
        beams = new ArrayList<>();
    }

    public void addBeam(int startIndex, int endIndex, int number) {
        beams.add(new Beam(startIndex, endIndex, number));
    }

    @Override
    public <Anchor> void draw(MusicCanvas<Anchor> canvas, Map<Chord, ChordAnchors<Anchor>> chordAnchorsMap) {
        // decide to draw the not stem upwards or downwards
        int sign = RenderingConfiguration.upwardStems ? 1 : -1;
        for (Chord chord : chords) {
            chord.computeAnchors(canvas, chordAnchorsMap);
        }
        if (chords.size() > 1) {
            // draw the implicit first level beam
            Chord firstChord = chords.get(0);
            ChordAnchors<Anchor> firstChordAnchors = chordAnchorsMap.get(firstChord);
            Chord lastChord = chords.get(chords.size() - 1);
            ChordAnchors<Anchor> lastChordAnchors = chordAnchorsMap.get(lastChord);
            // dividing the beam width by two to align the beams with the stems
            canvas.drawBeam(
                    firstChordAnchors.stemEnd(),
                    0, -sign * RenderingConfiguration.beamWidth / 2,
                    lastChordAnchors.stemEnd(),
                    0, -sign * RenderingConfiguration.beamWidth / 2,
                    RenderingConfiguration.beamWidth);
            // adjust middle chord anchors to account for the beam
            for (Chord chord : chords.subList(1, chords.size() - 1)) {
                ChordAnchors<Anchor> chordAnchors = chordAnchorsMap.get(chord);
                float interpolatePos = (chord.getMusicalPosition().crotchetsIntoLine()
                        - firstChord.getMusicalPosition().crotchetsIntoLine())
                        / (lastChord.getMusicalPosition().crotchetsIntoLine()
                        - firstChord.getMusicalPosition().crotchetsIntoLine());

                Anchor interpolatedAnchor = canvas.interpolateAnchors(firstChordAnchors.stemEnd(), lastChordAnchors.stemEnd(), interpolatePos);
                ChordAnchors<Anchor> middleChordAnchors = new ChordAnchors<>(chordAnchors.lowestNotehead(), chordAnchors.highestNotehead(),
                        interpolatedAnchor, chordAnchors.noteheadOffset(), chordAnchors.stemEndOffset());
                chordAnchorsMap.put(chord, middleChordAnchors);
            }

            for (Beam beam : beams) {
                float beamOffset = -(sign * beam.number * RenderingConfiguration.beamWidth
                        + sign * RenderingConfiguration.gapBetweenBeams * beam.number);
                if (beam.startIndex == beam.endIndex) {
                    // canvas.drawBeam(chordAnchorsMap.get(chords.get(beam.startIndex)).stemEnd(),
                    // -1, 3.125f - 1f * beam.number,
                    // chordAnchorsMap.get(chords.get(beam.endIndex)).stemEnd(), 0, 3.125f - 0.8f *
                    // beam.number,
                    // 0.75f);
                    if (beam.startIndex == 0) {
                        System.out.println("Drawing what should become a flag");
                    } else {
                        canvas.drawBeam(
                                canvas.interpolateAnchors(
                                        chordAnchorsMap.get(chords.get(beam.startIndex)).stemEnd(),
                                        chordAnchorsMap.get(chords.get(beam.startIndex - 1)).stemEnd(),
                                        RenderingConfiguration.hookRatio * chords.get(beam.startIndex).noteType.defaultLengthInCrotchets * 2
                                ),
                                0,
                                beamOffset,
                                chordAnchorsMap.get(chords.get(beam.startIndex)).stemEnd(),
                                0,
                                beamOffset,
                                RenderingConfiguration.beamWidth);
                    }
                    // TODO: implement beamlets here?
                } else {
                    if (RenderingConfiguration.allHooked && beam.startIndex != 0) {
                        canvas.drawBeam(
                                canvas.interpolateAnchors(
                                        chordAnchorsMap.get(chords.get(beam.startIndex)).stemEnd(),
                                        chordAnchorsMap.get(chords.get(beam.startIndex - 1)).stemEnd(),
                                        RenderingConfiguration.hookRatio * chords.get(beam.startIndex).noteType.defaultLengthInCrotchets * 2
                                ),
                                0,
                                beamOffset,
                                chordAnchorsMap.get(chords.get(beam.endIndex)).stemEnd(), 0,
                                beamOffset,
                                RenderingConfiguration.beamWidth);
                    } else {
                        canvas.drawBeam(
                                chordAnchorsMap.get(chords.get(beam.startIndex)).stemEnd(),
                                0,
                                beamOffset,
                                chordAnchorsMap.get(chords.get(beam.endIndex)).stemEnd(), 0,
                                beamOffset,
                                RenderingConfiguration.beamWidth);
                    }
                }
            }

        }
        for (Chord chord : chords) {
            chord.draw(canvas, chordAnchorsMap);
        }

//        if (chords.size() == 1) {
//            Anchor note = chords.get(0).drawRetAnchor(canvas);
//            canvas.drawBeam(note, -1, 3, 0, 3, 0.75f);
//        } else {
//            List<Anchor> anchors = new ArrayList<Anchor>();
//            Anchor start = chords.get(0).drawRetAnchor(canvas);
//            Anchor end = chords.get(chords.size() - 1).drawRetAnchor(canvas);
//            anchors.add(start);
//            canvas.drawBeam(start, 0, 3.125f, end, 0, 3.125f, 0.75f);
//            for (Chord chord : chords.subList(1, chords.size() - 1)) {
//                anchors.add(chord.drawRetAnchor(canvas));
//            }
//            anchors.add(end);
//            for (Beam beam : beams) {
//                if (beam.startIndex == beam.endIndex) {
//                    canvas.drawBeam(anchors.get(beam.startIndex), -1, 3.125f - 1f * beam.number,
//                            anchors.get(beam.endIndex), 0, 3.125f - 0.8f * beam.number, 0.75f);
//                } else {
//                    canvas.drawBeam(anchors.get(beam.startIndex), 0, 3.125f - 1f * beam.number,
//                            anchors.get(beam.endIndex), 0, 3.125f - 1f * beam.number, 0.75f);
//                }
//            }
//        }
    }
}
