package com.bn.Struct2;


public class CFace {
	private CLoop	loop_h;
	private static int id=0;
	
	public int		face_id;
	public CSolid	host_s;
	public CFace	next;
	public CFace	prev;
	public Vector3f	feq;
	public CFace()
	{
		face_id = id++;
		feq=new Vector3f(0,0,0);
		host_s = null;
		loop_h = null;
		next = prev = null;
	}
	
	public CLoop GetLoopHead() 
	{ 
		return loop_h;
	}
	
	public boolean AddLoop(CLoop ploop)
	{
		if(ploop==null)
		{
			return false;
		}

		CLoop ptemp = loop_h;

		if(ptemp==null)
			loop_h = ploop;
		else
		{
			while(ptemp.next!=null)
			{
				ptemp = ptemp.next;
			}
			ptemp.next = ploop;
			ploop.prev = ptemp;
		}
		ploop.host_f = this;

		return true;
	}

}
