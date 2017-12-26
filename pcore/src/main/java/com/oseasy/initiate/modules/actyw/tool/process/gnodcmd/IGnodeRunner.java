package com.oseasy.initiate.modules.actyw.tool.process.gnodcmd;

import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.cmd.IcmdPtpl;

public interface IGnodeRunner<T, K extends IcmdPtpl> {
  public Gcmd getCmd();
  public ActYwRstatus<T> exec(K param);
}
