package uk.ac.cam.optimisingmusicnotation.representation.properties;

import uk.ac.cam.optimisingmusicnotation.rendering.MusicCanvas;
import uk.ac.cam.optimisingmusicnotation.representation.Line;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class KeySignature {

    public void addAlteration(int index, PitchName pitch, Accidental alteration) {
        alterations.add(index, new Alteration(pitch, alteration));
    }

    public void addAlteration(PitchName pitch, Accidental alteration) {
        alterations.add(new Alteration(pitch, alteration));
    }

    public List<Alteration> getAlterations() {
        return alterations;
    }

    public Accidental getAccidental(PitchName pitchName) {
        for (Alteration alt : alterations) {
            if (alt.alteredPitch == pitchName) {
                return alt.getAccidental();
            }
        }
        return Accidental.NONE;
    }

    private final List<Alteration> alterations;

    public KeySignature () {
        this.alterations = new ArrayList<>();
    }

    // Key signature has a list of alterations for each pitch type
    // They are added in the order they are expected to be drawn in
    // For example, D major would have the following alterations:
    // [(PitchName.F, Accidental.SHARP), (PitchName.C, Accidental.SHARP)]
    // Note that when C major/A minor is parsed, it does add all the relevant naturals so please do draw them
    public KeySignature (List<PitchName> pitches, List<Accidental> accidentals) {
        this.alterations = new ArrayList<>();
        for (int i = 0; i < pitches.size() && i < accidentals.size(); ++i) {
            this.alterations.add(new Alteration(pitches.get(i), accidentals.get(i)));
        }
    }

    public <Anchor> void draw(MusicCanvas<Anchor> canvas, Line line, Clef clef){
        int numAlterations = alterations.size();
        for (int i = 0; i < numAlterations; i++) {
            alterations.get(i).draw(canvas, new MusicalPosition(line,0), clef,i, alterations.size());
        }
    }

    public static class Alteration {
        public PitchName getAlteredPitch() {
            return alteredPitch;
        }

        PitchName alteredPitch;

        public Accidental getAccidental() {
            return accidental;
        }

        Accidental accidental;

        public Alteration(PitchName alteredPitch, Accidental accidental) {
            this.alteredPitch = alteredPitch;
            this.accidental = accidental;
        }

        public <Anchor> void draw(MusicCanvas<Anchor> canvas, MusicalPosition position, Clef clef, int numAcross, int numAlterations){
            List<Pitch> pitches;
            try {
                pitches = clef.pitchNameToPitches(alteredPitch);
                Pitch firstPitch = pitches.get(0);
                Pitch lastPitch = pitches.get(pitches.size()-1);
                String path = RenderingConfiguration.imgFilePath + "/accidentals/" + accidental.toString().toLowerCase() + ".svg";
                canvas.drawImage(path, canvas.getLineStartAnchor(position,lastPitch),-(2f+numAlterations)+numAcross, accidental == Accidental.SHARP ? 1.3f: 1.9f,0,2.6f);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
