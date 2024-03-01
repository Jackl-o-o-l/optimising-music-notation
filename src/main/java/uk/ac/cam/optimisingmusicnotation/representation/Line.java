package uk.ac.cam.optimisingmusicnotation.representation;

import uk.ac.cam.optimisingmusicnotation.rendering.MusicCanvas;
import uk.ac.cam.optimisingmusicnotation.representation.beatlines.BarLine;
import uk.ac.cam.optimisingmusicnotation.representation.beatlines.PulseLine;
import uk.ac.cam.optimisingmusicnotation.representation.properties.*;

import java.util.ArrayList;
import java.util.List;

public class Line {
  
    public List<Stave> getStaves() {
        return staves;
    }

    private final List<Stave> staves;
    public List<PulseLine> getPulseLines() {
        return pulseLines;
    }


    public void addPulseLine(PulseLine pulseLine) {
        pulseLines.add(pulseLine);
    }

    private final List<PulseLine> pulseLines;

    public int getLineNumber() {
        return lineNumber;
    }

    private final int lineNumber;

    public float getEndInCrotchets() { return lengthInCrotchets + offsetInCrochets; }

    public float getLengthInCrotchets() {
        return lengthInCrotchets;
    }
  
    private final float lengthInCrotchets;

    public float getOffsetInCrotchets() {
        return offsetInCrochets;
    }

    private final float offsetInCrochets;

    public float getStartTimeInCrotchets() {
        return startTimeInCrotchets;
    }

    private final float startTimeInCrotchets;
  
    public Line(List<Stave> staves, float startTimeInCrotchets, float lengthInCrochets, float offsetInCrochets, int lineNumber) {
        this.staves = staves;
        this.lineNumber = lineNumber;
        this.startTimeInCrotchets = startTimeInCrotchets;
        this.lengthInCrotchets = lengthInCrochets;
        this.offsetInCrochets = offsetInCrochets;
        pulseLines = new ArrayList<>();
    }

    public <Anchor> void draw(MusicCanvas<Anchor> canvas) {
        for (PulseLine p: pulseLines) {
            p.drawAboveStave(canvas);
        }
        for (Stave s: staves) {
            s.draw(canvas,this);
        }
        drawTimeSignatures(canvas);
    }

    private <Anchor> void drawTimeSignatures(MusicCanvas<Anchor> canvas){
        TimeSignature lastTimeSig = null;
        TimeSignature currentTimeSig;
        for (PulseLine p: pulseLines) {
            if (p instanceof BarLine){
                currentTimeSig = ((BarLine) p).getTimeSignature();
                if (currentTimeSig != null && !(currentTimeSig.equals(lastTimeSig))){
                    lastTimeSig = ((BarLine) p).getTimeSignature();
                    lastTimeSig.draw(canvas,p.getMusicalPosition());
                }
            }
        }
    }
}