package com.groupon.seleniumgridextras.utilities.threads.video;

import com.groupon.seleniumgridextras.grid.proxies.SetupTeardownProxy;
import com.groupon.seleniumgridextras.utilities.json.JsonCodec;
import com.groupon.seleniumgridextras.videorecording.RemoteVideoRecorderHelper;
import org.apache.log4j.Logger;
import org.openqa.grid.internal.TestSession;

import java.util.concurrent.Callable;

public class RemoteVideoRecordingControlCallable implements Callable {

    private static final int SECONDS_TO_WAIT_FOR_EXTERNAL_KEY = 120;
    private final String action;
    private String lastAction;
    private TestSession session;
    private SetupTeardownProxy proxy;
    private static Logger logger = Logger.getLogger(RemoteVideoRecordingControlCallable.class);

    public RemoteVideoRecordingControlCallable(SetupTeardownProxy proxy, TestSession session, String action, String lastAction) {
        logger.info(
                String.format(
                        "Creating new Video callable for proxy: %s, int key: %s, ex. key: %s, action: %s, lastAction: %s",
                        proxy.getId(),
                        session.getInternalKey(),
                        (session.getExternalKey() != null ? session.getExternalKey().getKey() : "[no key assigned yet]"),
                        action,
                        lastAction));

        this.session = session;
        this.proxy = proxy;
        this.action = action;
        this.lastAction = lastAction;

    }

    public RemoteVideoRecordingControlCallable(SetupTeardownProxy proxy, TestSession session, String action) {
        this(proxy, session, action, "");
    }

    @Override
    public String call() throws Exception {
        if (!acquiredExternalKey()) {
            return null; //If we haven't acquired an external session at this point, there is no point to continue
        }


        String message;
        if (this.action.equals(JsonCodec.Video.START)) { //My kingdom for a switch statement!
            message = startVideo();

            if (!message.equals("")) {
                //Fail safe, in case the start command was not successful, we don't consider the video as already
                //recording
                this.proxy.getSessionsRecording().add(this.session.getExternalKey().getKey());

                //If video recording started, send the desired capabilities to be put in lower 1/3
                this.lastAction = session.getRequestedCapabilities().toString();
                updateLastAction();
            }

        } else if (this.action.equals(JsonCodec.Video.STOP)) {
            message = stopVideo();
        } else if (this.action.equals(JsonCodec.Video.HEARTBEAT)) {
            message = updateLastAction();
        } else {
            message = String.format("Unrecognized action: %s, for session: %s, will not send a video request.",
                    this.action,
                    this.session.getExternalKey().getKey());
            logger.error(message);
            return message; // early return so we don't double print the message as warning and info

        }
        logger.debug(message);
        return message;


    }

    protected boolean acquiredExternalKey() {
        if (this.session.getExternalKey() != null) {
            return true;
        }

        for (int i = 0; i < SECONDS_TO_WAIT_FOR_EXTERNAL_KEY; i++) {
            try {
                Thread.sleep(1000);
                if (this.session.getExternalKey() != null) {
                    break;
                }
            } catch (InterruptedException e) {
                logger.error(String.format("%s for int. key: %s, proxy: %s",
                        e.getMessage(),
                        this.session.getInternalKey(),
                        this.proxy.getId()));
            }

        }

        if (this.session.getExternalKey() == null) {
            logger.warn(
                    String.format(
                            "Waited for %s seconds for int key: , to get external key and timed out. Will skip callable",
                            this.session.getInternalKey()));
            return false;
        }

        return true;
    }

    protected String updateLastAction() {
        String m = RemoteVideoRecorderHelper.updateLastAction(
                this.session.getSlot().getRemoteURL().getHost(),
                this.session.getSlot().getProxy().getConfig().custom.get("grid-extras-port"),
                this.session.getExternalKey().getKey(),
                this.lastAction);

        logger.debug(m);
        return m;
    }

    protected String stopVideo() {
        String m = RemoteVideoRecorderHelper.stopVideoRecording(
                this.session.getSlot().getRemoteURL().getHost(),
                this.session.getSlot().getProxy().getConfig().custom.get("grid-extras-port"),
                this.session.getExternalKey().getKey());

        logger.debug(m);

        return m;
    }

    protected String startVideo() {
        return RemoteVideoRecorderHelper.startVideoRecording(
                this.session.getSlot().getRemoteURL().getHost(),
                this.session.getSlot().getProxy().getConfig().custom.get("grid-extras-port"),
                this.session.getExternalKey().getKey());
    }


}
