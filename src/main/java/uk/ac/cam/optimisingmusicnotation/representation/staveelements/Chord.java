package uk.ac.cam.optimisingmusicnotation.representation.staveelements;

import uk.ac.cam.optimisingmusicnotation.rendering.MusicCanvas;
import uk.ac.cam.optimisingmusicnotation.representation.properties.Accidental;
import uk.ac.cam.optimisingmusicnotation.representation.properties.Pitch;
import uk.ac.cam.optimisingmusicnotation.representation.properties.RenderingConfiguration;
import uk.ac.cam.optimisingmusicnotation.representation.staveelements.chordmarkings.ChordMarking;

import java.util.ArrayList;
import java.util.List;

public class Chord extends BeamGroup {
    protected final List<Note> notes;
    protected final List<ChordMarking> markings;
    protected final float crotchetsIntoLine;

    public Chord() {
        notes = new ArrayList<>();
        markings = new ArrayList<>();
        crotchetsIntoLine = 0;
    };

    public Chord(List<Pitch> pitches, List<Accidental> accidentals, float crotchetsIntoLine) {
        notes = new ArrayList<>(pitches.size());
        for (int i = 0; i < pitches.size(); ++i) {
            notes.add(new Note(pitches.get(i), accidentals.get(i)));
        }
        markings = new ArrayList<>();
        this.crotchetsIntoLine = crotchetsIntoLine;
    }

    public void addMarking(ChordMarking marking) {
        markings.add(marking);
    }

    @Override
    public <Anchor> void draw(MusicCanvas<Anchor> canvas, RenderingConfiguration config) {

    }

    private static class Note {
        Pitch pitch;
        Accidental accidental;

        public Note(Pitch pitch, Accidental accidental) {
            this.pitch = pitch;
            this.accidental = accidental;
        }
    }
}
