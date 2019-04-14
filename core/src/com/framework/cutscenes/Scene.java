package com.framework.cutscenes;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

public class Scene extends Actor {

    private Array<SceneSegment> segments;
    private int index;

	/*------------------------------------------------------------------*\
	|*							Initialization							*|
	\*------------------------------------------------------------------*/

    public Scene() {
        super();
        segments = new Array<SceneSegment>();
        index = -1;
    }

	/*------------------------------------------------------------------*\
	|*							Public Methods 							*|
	\*------------------------------------------------------------------*/

    @Override
    public void act(float dt) {
	    if (isSegmentFinished() && !isLastSegment()) loadNextSegment();
    }

	public void addSegment(SceneSegment segment) {
	    segments.add(segment);
    }

    public void clearSegments() {
	    segments.clear();
    }

    public void start() {
	    index = 0;
	    segments.get(index).start();
    }

    public void loadNextSegment() {
	    if (isLastSegment()) return;

	    segments.get(index).finish();
	    index++;
	    segments.get(index).start();
    }

    public boolean isSegmentFinished() {
	    return segments.get(index).isFinished();
    }

    public boolean isLastSegment() {
	    return (index >= segments.size - 1);
    }

    public boolean isSceneFinished() {
        return (isLastSegment() && isSegmentFinished());
    }
}
