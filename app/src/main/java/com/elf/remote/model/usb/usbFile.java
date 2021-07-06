package com.elf.remote.model.usb;

public class usbFile {
    public int Id;
    public String usbUri;
    public int usbName;

    public int getUsbName() {
        return usbName;
    }

    public void setUsbName(int usbName) {
        this.usbName = usbName;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getUsbUri() {
        return usbUri;
    }

    public void setUsbUri(String usbUri) {
        this.usbUri = usbUri;
    }
}
