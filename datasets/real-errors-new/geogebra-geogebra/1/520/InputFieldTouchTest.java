package org.geogebra.common.euclidian;

import org.geogebra.common.euclidian.draw.DrawInputBox;
import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.jre.headless.LocalizationCommon;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.AppCommon3D;
import org.geogebra.test.euclidian.AutoCompleteTextFieldC;
import org.geogebra.test.euclidian.TextFieldCommonJre;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class InputFieldTouchTest {

	private static AppCommon3D app;
	private static EuclidianView ev;

	private GeoInputBox input1;
	private GeoInputBox input2;
	private ViewTextField viewTextField = new TextFieldCommonJre(null);
	private AutoCompleteTextFieldC textField;

	@Before
	public void setUp() {
		app = new AppCommon3D(new LocalizationCommon(3),
				new AwtFactoryCommon());
		ev = app.getActiveEuclidianView();
		ev.setViewTextField(viewTextField);

		input1 = addInputBox("inputbox1");
		input2 = addInputBox("inputbox2");
		textField = (AutoCompleteTextFieldC) viewTextField.getTextField();
	}

	@Test
	public void applyOnSwitchInputBoxesTest() {
		DrawInputBox drawInputBox1 = (DrawInputBox) ev.getDrawableFor(input1);
		DrawInputBox drawInputBox2 = (DrawInputBox) ev.getDrawableFor(input2);
		viewTextField.focusTo(drawInputBox1);
		textField.setText("ABC");
		viewTextField.focusTo(drawInputBox2);
		textField.setText("DEF");
		viewTextField.focusTo(drawInputBox1);
		Assert.assertEquals("ABC", input1.getText());
		Assert.assertEquals("DEF", input2.getText());
	}

	@Test
	public void applyOnClickOutOfInputBoxTest() {
		DrawInputBox drawInputBox1 = (DrawInputBox) ev.getDrawableFor(input1);
		viewTextField.focusTo(drawInputBox1);
		textField.setText("ABC");
		textField.blur();
		Assert.assertEquals("ABC", input1.getText());
	}

	private GeoInputBox addInputBox(String name) {
		String command = name + " = InputBox()";
		GeoElementND[] results = app.getKernel().getAlgebraProcessor()
				.processAlgebraCommand(command,	false);

		return (GeoInputBox)results[0];
	}
}
