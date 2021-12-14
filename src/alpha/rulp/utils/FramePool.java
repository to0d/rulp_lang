package alpha.rulp.utils;

import static alpha.rulp.lang.Constant.I_FRAME_ID_MIN;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.ximpl.runtime.XRFrame;

public class FramePool {

	private int globalFrameMaxId = I_FRAME_ID_MIN;

	private final XRFrame[] globalFramePool1;

	private ArrayList<XRFrame> globalFramePool2 = new ArrayList<>();

	private LinkedList<Integer> globalFreeFrameIdList = new LinkedList<>();

	private final int pool1Len;

	public FramePool(int pool1Len) {
		super();
		this.pool1Len = pool1Len;
		this.globalFramePool1 = new XRFrame[pool1Len];
	}

	public synchronized void allocateFrameId(XRFrame frame) throws RException {

		int nextFrameId = -1;

		if (!globalFreeFrameIdList.isEmpty()) {
			nextFrameId = globalFreeFrameIdList.pollLast();
		} else {
			nextFrameId = globalFrameMaxId++;
		}

		/*********************************************/
		// Allocate from pool1
		/*********************************************/
		if (nextFrameId < pool1Len) {

			if (globalFramePool1[nextFrameId] != null) {
				throw new RException("Global frame pool 1 is not clear at: " + nextFrameId);
			}

			globalFramePool1[nextFrameId] = frame;

		}
		/*********************************************/
		// Allocate from pool2
		/*********************************************/
		else {

			int pool2Index = nextFrameId - pool1Len;

			if (pool2Index < globalFramePool2.size()) {

				if (globalFramePool2.get(pool2Index) != null) {
					throw new RException("Global frame pool 2 is not clear at: " + nextFrameId);
				}

				globalFramePool2.set(pool2Index, frame);

			} else {

				while (globalFramePool2.size() < pool2Index) {
					globalFramePool2.add(null);
				}

				globalFramePool2.add(frame);
			}
		}

		frame.setFrameId(nextFrameId);
		RType.FRAME.incCreateCount();
	}

	public synchronized void freeFrameId(int frameId) throws RException {

		/*********************************************/
		// free to pool1
		/*********************************************/
		if (frameId < pool1Len) {

			if (globalFramePool1[frameId] == null) {
				// throw new RException("Frame not found in pool1: " + frameId);
				return;
			}

			globalFramePool1[frameId] = null;

		}
		/*********************************************/
		// free to pool2
		/*********************************************/
		else {

			int pool2Index = frameId - pool1Len;

			if (pool2Index >= globalFramePool2.size()) {
				throw new RException("Invalid frame id: " + frameId);
			}

			if (globalFramePool2.get(pool2Index) == null) {
				throw new RException("Frame id not found: " + frameId);
			}

			globalFramePool2.set(pool2Index, null);
		}

		globalFreeFrameIdList.push(frameId);
	}

	public synchronized int getFrameFreeIdCount() {
		return globalFreeFrameIdList.size();
	}

	public synchronized int getFrameMaxId() {
		return globalFrameMaxId;
	}

	public synchronized List<IRFrame> listGlobalFrames() {

		List<IRFrame> frames = new LinkedList<>();

		for (XRFrame frame : globalFramePool1) {
			if (frame != null) {
				frames.add(frame);
			}
		}

		for (XRFrame frame : globalFramePool2) {
			if (frame != null) {
				frames.add(frame);
			}
		}

		return frames;
	}
}
