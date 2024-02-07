package uk.ac.cam.optimisingmusicnotation.representation.whitespaces;

import uk.ac.cam.optimisingmusicnotation.rendering.MusicCanvas;
import uk.ac.cam.optimisingmusicnotation.representation.properties.RenderingConfiguration;

public interface Whitespace {
    <Anchor> void draw(MusicCanvas<Anchor> canvas, RenderingConfiguration config);
}
