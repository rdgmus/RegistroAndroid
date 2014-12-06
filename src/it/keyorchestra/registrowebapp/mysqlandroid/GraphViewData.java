package it.keyorchestra.registrowebapp.mysqlandroid;

import com.jjoe64.graphview.GraphViewDataInterface;

public class GraphViewData implements GraphViewDataInterface {
	double _x, _y;

	public GraphViewData(double x, double y) {
		// TODO Auto-generated constructor stub
		this._x = x;
		this._y = y;
	}

	@Override
	public double getX() {
		// TODO Auto-generated method stub
		return _x;
	}

	@Override
	public double getY() {
		// TODO Auto-generated method stub
		return _y;
	}

}
