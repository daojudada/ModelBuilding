package com.bn.Object;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Vector;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.bn.Main.MatrixState;
import com.bn.Main.MySurfaceView;
import com.bn.Main.ShaderManager;
import com.bn.Struct1.Bound;
import com.bn.Struct1.Quaternion;
import com.bn.Struct1.Struct.Vector3f;
import com.bn.Util.LoadUtil;


public class Solid extends Body
{
	private float[] stlVec;	   //STL格式输出的最终格式
	private float[] stlNormals;//STL格式输出的初始每个三角面片的法向量	
	private float[] stlVertexs;//STL格式输出的初始每个三角面片的顶点坐标
	
	private Vector<Integer> indices;
	private Vector<Vector3f> vertices;
	private int normalMode;

	
	int mProgram;
    int muMVPMatrixHandle;//总变换矩阵引用
    int maPositionHandle; //顶点位置属性引用
    int maTexCoorHandle; //顶点纹理坐标属性引用
    int maColorHandle;//顶点颜色属性引用
    int muMMatrixHandle;//位置、旋转变换矩阵
    int muProjCameraMatrixHandle;//投影、摄像机组合矩阵引用
    
    int maCameraHandle; //摄像机位置属性引用 
    int maNormalHandle; //顶点法向量属性引用 
    int maLightLocationHandle;//光源位置属性引用  
    int muIsShadow;//是否绘制阴影属性引用
    
    String mVertexShader;//顶点着色器代码脚本  	 
    String mFragmentShader;//片元着色器代码脚本
	
    FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
	FloatBuffer   mNormalBuffer;//顶点法向量数据缓冲
	FloatBuffer   mColorBuffer;//顶点法向量数据缓冲


	
    public Solid(MySurfaceView mv,Vector<Vector3f> vSet,Vector<Integer> iSet,int Mode)
    {    	
    	la=new Axis();
    	la.initShader(ShaderManager.getLineShaderProgram());
    	//Box =new Bound(vSet);
    	
    	vertices=vSet;
    	indices=iSet;	
    	normalMode=Mode;
    	
    	//获取一开始模型的三角面片
    	stlNormals=LoadUtil.getStlNolmalsOnlyFace(vertices, indices);
    	stlVertexs=LoadUtil.getVerticesOnlyFace(vertices, indices);
    	initPoint(stlNormals,stlVertexs);
    	
    	//初始化顶点和法向量数组
    	if(normalMode==1)
    	{
    		normals=LoadUtil.getNolmalsOnlyAverage(vertices, indices);
    		vertexs=LoadUtil.getVerticesOnlyAverage(vertices, indices);
    	}
    	else if(normalMode==2)
    	{
    		normals=LoadUtil.getNolmalsOnlyFace(vertices, indices);
    		vertexs=LoadUtil.getVerticesOnlyFace(vertices, indices);
    	}
    	
    	Box=new Bound(vertexs);
    	//初始化顶点坐标与着色数据
    	initVertexData();
    	initShader(ShaderManager.getShadowshaderProgram());
    }
    

    
    public Solid(MySurfaceView mv,Body b)
    {    	
    	la=new Axis();
    	la.initShader(ShaderManager.getLineShaderProgram());
    	
    	Solid s=(Solid)b;
    	vertices=s.getVertices();
    	indices=s.getIndices();	
    	normalMode=s.getNormalMode();
    	
    	stlNormals=LoadUtil.getStlNolmalsOnlyFace(vertices, indices);
    	stlVertexs=LoadUtil.getVerticesOnlyFace(vertices, indices);
    	initPoint(stlNormals,stlVertexs);
    	
    	quater = new Quaternion(b.quater);
	    xLength=b.xLength;//绕x轴平移距离
	    yLength=b.yLength;//绕y轴平移距离
	    zLength=b.zLength;//绕x轴平移距离
	    xScale=b.xScale;//绕x轴放缩倍数
	    yScale=b.yScale;//绕y轴放缩倍数
	    zScale=b.zScale;//绕z轴放缩倍数 
	    
	    normals=b.normals;
		vertexs=b.vertexs;
	    
		Box=new Bound(vertexs);
    	//初始化顶点坐标与着色数据
    	initVertexData();
    	initShader(ShaderManager.getShadowshaderProgram());
    }
    
    private void initPoint(float[] n,float[] v){
    	stlVec=new float[stlNormals.length+stlVertexs.length];
    	int num=0,num1=0,num2=0;//计数专用
    	for(int i=0;i<stlVec.length;i+=12)
    	{
    		for(int j=0;j<3;j++)
    			stlVec[num++]=stlNormals[num1++];
    		for(int k=0;k<9;k++)
    			stlVec[num++]=stlVertexs[num2++];
    	}
    }
   
    public  float[] getStlPoint(){
    	float[] tempVec=new float[stlVec.length];
    	for(int i=0;i<stlVec.length;i+=3)
    	{
    		float[] cur=new float[4];
    		//求变换后的点
    		Matrix.multiplyMV(cur, 0, getM(), 0, new float[]{stlVec[i],stlVec[i+1],stlVec[i+2],1}, 0);
    		tempVec[i]=cur[0];
    		tempVec[i+1]=cur[1];
    		tempVec[i+2]=cur[2];
    	}
    	
    	return tempVec;  			
    }
    
    
    
  
    //初始化顶点坐标与着色数据的方法
    @Override
    public void initVertexData()
    {
    	
    	
    	//顶点坐标数据的初始化================begin============================
    	vCount=vertexs.length/3;   
		
        //创建顶点坐标数据缓冲
        //vertices.length*4是因为一个整数四个字节
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertexs.length*4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为Float型缓冲
        mVertexBuffer.put(vertexs);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点坐标数据的初始化================end============================
        
        //顶点法向量数据的初始化================begin============================  
        ByteBuffer nbb = ByteBuffer.allocateDirect(normals.length*4);
        nbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mNormalBuffer = nbb.asFloatBuffer();//转换为Float型缓冲
        mNormalBuffer.put(normals);//向缓冲区中放入顶点法向量数据
        mNormalBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点着色数据的初始化================end============================
        
        float[] colors=new float[vCount*4];
        for(int i=0;i<vCount;i++)
        {
        	colors[4*i]=0.60f;
        	colors[4*i+1]=0.70f;
        	colors[4*i+2]=0.90f;
        	colors[4*i+3]=1.00f;
        }
        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length*4);
        cbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mColorBuffer = cbb.asFloatBuffer();//转换为Float型缓冲
        mColorBuffer.put(colors);//向缓冲区中放入顶点法向量数据
        mColorBuffer.position(0);//设置缓冲区起始位置
    }
	
	
	//初始化shader
    @Override
    public void initShader(int Program)
    {
        //基于顶点着色器与片元着色器创建程序
        mProgram = Program;
        //获取程序中顶点位置属性引用  
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点发向量属性引用  
        maNormalHandle= GLES20.glGetAttribLocation(mProgram, "aNormal");
        //获取程序中顶点颜色属性引用id  
        maColorHandle= GLES20.glGetAttribLocation(mProgram, "aColor");
        //获取程序中视角变换矩阵
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");  
        //获取位置、旋转变换矩阵引用
        muMMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMMatrix"); 
        //获取程序中投影、摄像机组合矩阵引用
        muProjCameraMatrixHandle=GLES20.glGetUniformLocation(mProgram, "uMProjCameraMatrix"); 
        //获取程序中光源位置引用
        maLightLocationHandle=GLES20.glGetUniformLocation(mProgram, "uLightLocation");
        //获取程序中摄像机位置引用
        maCameraHandle=GLES20.glGetUniformLocation(mProgram, "uCamera");  
        //获取程序中是否绘制阴影属性引用
        muIsShadow=GLES20.glGetUniformLocation(mProgram, "isShadow"); 
    }
    
    public void drawSelf(int isShadow)
    {      
	    
	    
    	setBody();
    	
    	if(isChoosed&&isShadow==0)
 	    {
 	    	//绘制坐标轴
 			MatrixState.pushMatrix();
 			la.drawSelf();		
 			MatrixState.popMatrix();
 	    }
    	
    	 //制定使用某套着色器程序
    	 GLES20.glUseProgram(mProgram);
         //将最终变换矩阵传入着色器程序
         GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0); 
         //将位置、旋转变换矩阵传入着色器程序
         GLES20.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);   
         //将投影、摄像机组合矩阵传入着色器程序
         GLES20.glUniformMatrix4fv(muProjCameraMatrixHandle, 1, false, MatrixState.getViewProjMatrix(), 0);   
         //将光源位置传入着色器程序   
         GLES20.glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB);
         //将摄像机位置传入着色器程序   
         GLES20.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);
         //将是否绘制阴影属性传入着色器程序 
         GLES20.glUniform1i(muIsShadow, isShadow);         
         //将顶点位置数据传入渲染管线
         GLES20.glVertexAttribPointer  
         (
         		maPositionHandle,   
         		3, 
         		GLES20.GL_FLOAT, 
         		false,
                3*4,   
                mVertexBuffer
         );       
         //将顶点法向量数据传入渲染管线
         GLES20.glVertexAttribPointer  
         (
        		maNormalHandle, 
         		3,   
         		GLES20.GL_FLOAT, 
         		false,
                3*4,   
                mNormalBuffer
         );   
         //将顶点颜色向量数据传入渲染管线
         GLES20.glVertexAttribPointer  
         (
        		maColorHandle, 
         		3,   
         		GLES20.GL_FLOAT, 
         		false,
                4*4,   
                mColorBuffer
         );   
         //启用顶点位置、法向量数据
         GLES20.glEnableVertexAttribArray(maPositionHandle);  
         GLES20.glEnableVertexAttribArray(maNormalHandle);  
         GLES20.glEnableVertexAttribArray(maColorHandle);  
         
         //绘制加载的物体
         if(MySurfaceView.isFill)
         {        
             //绘制三角形
             GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount); 
         }
         else
         {
	         //绘制线条的粗细
	         GLES20.glLineWidth(2);
	         //绘制线框
	       //绘制加载的物体
	         GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, vCount); 
         }
    }

    public Vector<Vector3f> getVertices(){
		Vector<Vector3f> newVertices = new Vector<Vector3f>();

		for(int i = 0; i < vertices.size(); i++)
		{
			newVertices.add(vertices.get(i));
		}

		return newVertices;
	}
	public Vector<Integer> getIndices(){
		Vector<Integer> newIndices = new Vector<Integer>();

		for(int i = 0; i < indices.size(); i++)
		{
			newIndices.add(indices.get(i));
		}

		return newIndices;
	}
	public int getNormalMode(){

		return normalMode;
	}

	public boolean isEmpty(){
		if(indices.size() == 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}


	
	
	public Vector3f getMean(){
		Vector3f mean = new Vector3f(0,0,0);
		for(int i=0;i<vertices.size();i++)
		{
			mean.x += vertices.get(i).x;
			mean.y += vertices.get(i).y;
			mean.z += vertices.get(i).z;
		}
		mean.x /= vertices.size();
		mean.y /= vertices.size();
		mean.z /= vertices.size();
		
		return mean;
	}

	
}
