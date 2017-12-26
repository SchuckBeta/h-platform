package com.oseasy.initiate.common.ftp.exceptions;

public class FtpException extends RuntimeException {
  private static final long serialVersionUID = -2946266495682282677L;

  public FtpException(String message) {
    super(message);
  }

  public FtpException(Throwable e) {
    super(e);
  }

  public FtpException(String message, Throwable cause) {
    super(message, cause);
  }
}
