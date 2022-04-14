import com.neuronrobotics.bowlerstudio.physics.TransformFactory
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR
import com.neuronrobotics.sdk.common.Log

import eu.mihosoft.vrl.v3d.CSG
import eu.mihosoft.vrl.v3d.Cylinder
import eu.mihosoft.vrl.v3d.Hexagon
import eu.mihosoft.vrl.v3d.PropertyStorage
import eu.mihosoft.vrl.v3d.Transform
import javafx.scene.control.Slider
import javafx.scene.control.Tab
import javafx.scene.text.Text;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Affine
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

Slider getSlider(ArrayList<CSG> listOfObjects) {
	int numSteps=0
	for(CSG c:listOfObjects) {
		PropertyStorage incomingGetStorage = c.getStorage()
		if(incomingGetStorage.getValue("MaxAssemblyStep")!=Optional.empty()) {
			Integer max = incomingGetStorage.getValue("MaxAssemblyStep").get()
			if(max>numSteps) {
				numSteps=max
			}				
		}
	}
	
	Slider slider = new Slider(0, numSteps, numSteps);
	slider.setShowTickMarks(true);
	slider.setShowTickLabels(true);
	slider.setMajorTickUnit(0.25f);
	slider.setBlockIncrement(0.1f);
	
	
	
	slider.valueProperty().addListener(new ChangeListener<Number>() {
		public void changed(ObservableValue <?extends Number>observable, Number oldValue, Number newValue){
			int step = (int)newValue+1
			double fraction =-(newValue-step)
			
			for(CSG c:listOfObjects) {
				PropertyStorage incomingGetStorage = c.getStorage()
				String key = "AssemblySteps"
				if(incomingGetStorage.getValue(key)!=Optional.empty()) {
					HashMap<Integer,Transform> map=incomingGetStorage.getValue(key).get()
					boolean set=false
					TransformNR target=new TransformNR()
					for(int i=step;i<=numSteps;i++) {
						if(map.get(i)!=null) {
							def myScale= (i==step)?fraction:1
							def scaled =TransformFactory.csgToNR(map.get(i)).scale(myScale)
							target=target.times(scaled)
							//println c.getName()+" sliderval="+newValue+" step="+step+" fraction:"+myScale+" || "+i						
							TransformFactory.nrToAffine(target,incomingGetStorage.getValue("AssembleAffine").get());
							set=true;
						}
					}
					if(!set) {
						TransformFactory.nrToAffine(new TransformNR(),incomingGetStorage.getValue("AssembleAffine").get());
					}
					
				}
				
			}
		}
	 });
	return slider
}


CSG simpleSyntax =new Cylinder(10,40).toCSG() // a one line Cylinder

//create a Cylinder
CSG myCylinder = new Cylinder(10, // Radius at the bottom
                      		20, // Radius at the top
                      		40, // Height
                      		(int)30 //resolution
                      		).toCSG()//convert to CSG to display                    			         ).toCSG()//convert to CSG to display 
                      		.movey(50)
  //create a Cylinder
CSG pyramid = new Cylinder(	20, // Radius at the bottom
                      		0, // Radius at the top
                      		40, // Height
                      		(int)4 //resolution
                      		).toCSG()//convert to CSG to display                    			 
                      		.movex(50)
   //create a Cylinder
CSG hex = new Hexagon(	20, // Flat to flat radius
                      		40 // Height
                      		).toCSG()//convert to CSG to display                    			 
                      		.movex(50)
                      		.movey(50)
hex.addAssemblyStep( 2, new Transform().movez(30))
hex.addAssemblyStep( 1, new Transform().movex(30))
pyramid.addAssemblyStep( 4, new Transform().roty(90))
pyramid.addAssemblyStep( 5, new Transform().movez(30))
myCylinder.addAssemblyStep(5, new Transform().movez(30))
simpleSyntax.addAssemblyStep( 3, new Transform().movez(30))

hex.setName("Hex")
pyramid.setName("pyramid")
myCylinder.setName("myCylinder")
simpleSyntax.setName("simpleSyntax")
ArrayList<CSG> listOfObjects= [simpleSyntax,myCylinder ,pyramid,hex]



// Create a tab
Tab myTab = new Tab();
myTab.setText("Assembly Instructions")
Slider slider = getSlider(listOfObjects) 
//add content to the tab
myTab.setContent(slider);

return [listOfObjects,myTab]


                      		