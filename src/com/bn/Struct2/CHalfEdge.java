package com.bn.Struct2;


public class CHalfEdge {
	private CVertex		vtx;

	public CLoop		host_lp;
	public CEdge		edge;
	public CHalfEdge	next;
	public CHalfEdge	prev;
	public CHalfEdge	adj;

	public CHalfEdge()
	{
		edge = null;
		vtx = null;
		host_lp = null;
		next = null;
		prev = null;
		adj = null;
	}

	public CVertex GetVertex()
	{
		return vtx;
	}
	
	public boolean SetVertex(CVertex  pvert)
	{
		if(pvert==null)
		{
			return false;
		}

		vtx = pvert;

		return true;
	}
}