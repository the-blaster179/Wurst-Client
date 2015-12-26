package tk.wurst_client.navigator.gui;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import org.darkstorm.minecraft.gui.component.basic.BasicSlider;
import org.darkstorm.minecraft.gui.util.RenderUtil;
import org.lwjgl.input.Mouse;

import tk.wurst_client.commands.Cmd;
import tk.wurst_client.font.Fonts;
import tk.wurst_client.mods.Mod;
import tk.wurst_client.navigator.NavigatorItem;
import tk.wurst_client.utils.MiscUtils;

public class NavigatorFeatureScreen extends GuiScreen
{
	private int scroll = 0;
	private NavigatorItem item;
	private NavigatorScreen parent;
	private String type;
	private GuiButton primaryButton;
	private int scrollKnobPosition = 2;
	private boolean scrolling;
	private int textHeight;
	private String text;
	private SliderData[] sliderDatas = {};
	
	public NavigatorFeatureScreen(NavigatorItem item, NavigatorScreen parent)
	{
		this.item = item;
		this.parent = parent;
		
		if(item instanceof Mod)
			type = "Mod";
		else if(item instanceof Cmd)
			type = "Command";
		else
			type = "unknown";
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
	{
		// primary button
		String primaryAction = item.getPrimaryAction();
		primaryButton =
			new GuiButton(0, width / 2 - 152, height - 65, 100, 20,
				primaryAction);
		if(primaryAction.isEmpty())
			primaryButton.visible = false;
		buttonList.add(primaryButton);
		
		// keybind button
		buttonList.add(new GuiButton(1, width / 2 - 50, height - 65, 100, 20,
			"Add Keybind"));
		
		// tutorial button
		GuiButton tutorialButton =
			new GuiButton(2, width / 2 + 52, height - 65, 100, 20, "Tutorial");
		buttonList.add(tutorialButton);
		if(item.getTutorialPage().isEmpty())
			tutorialButton.visible = false;
		
		// type
		text = "Type: " + type;
		
		// description
		String description = item.getDescription();
		if(!description.isEmpty())
			text += "\n\nDescription:\n" + description;
		
		// area
		Rectangle area =
			new Rectangle((width / 2 - 154), 60, 308, (height - 103));
		
		// sliders
		ArrayList<BasicSlider> sliders = item.getSettings();
		if(!sliders.isEmpty())
		{
			// text
			text += "\n\nSettings:";
			sliderDatas = new SliderData[sliders.size()];
			for(int i = 0; i < sliders.size(); i++)
			{
				BasicSlider slider = sliders.get(i);
				text += "\n" + slider.getText() + ": ";
				switch(slider.getValueDisplay())
				{
					case DECIMAL:
						text += slider.getValue();
						break;
					case DEGREES:
						text += (int)slider.getValue() + "°";
						break;
					case INTEGER:
						text += (int)slider.getValue();
						break;
					case NONE:
						break;
					case PERCENTAGE:
						text += (slider.getValue() * 100D) + "%";
						break;
				}
				text += "\n";
				
				// data
				int y = area.y + Fonts.segoe15.getStringHeight(text);
				float value =
					(float)((slider.getValue() - slider.getMinimumValue()) / (slider
						.getMaximumValue() - slider.getMinimumValue()));
				int x = area.x + (int)((area.width - 10) * value);
				sliderDatas[i] = new SliderData(x, y, value);
			}
		}
		
		// text height
		textHeight = Fonts.segoe15.getStringHeight(text);
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		if(!button.enabled)
			return;
		
		switch(button.id)
		{
			case 0:
				item.doPrimaryAction();
				primaryButton.displayString = item.getPrimaryAction();
				break;
			case 1:
				
				break;
			case 2:
				MiscUtils.openLink("https://www.wurst-client.tk/wiki/"
					+ item.getTutorialPage());
				break;
		}
	}
	
	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}
	
	@Override
	protected void mouseClicked(int x, int y, int button) throws IOException
	{
		super.mouseClicked(x, y, button);
		
		if(new Rectangle(width / 2 + 170, 60, 12, height - 103).contains(x, y))
			scrolling = true;
	}
	
	@Override
	protected void mouseClickMove(int mouseX, int mouseY,
		int clickedMouseButton, long timeSinceLastClick)
	{
		if(scrolling && clickedMouseButton == 0)
		{
			int maxScroll = -textHeight + height - 146;
			if(maxScroll > 0)
				maxScroll = 0;
			
			if(maxScroll == 0)
				scroll = 0;
			else
				scroll =
					(int)((mouseY - 72) * (float)maxScroll / (height - 131));
			
			if(scroll > 0)
				scroll = 0;
			else if(scroll < maxScroll)
				scroll = maxScroll;
		}
	}
	
	@Override
	public void mouseReleased(int x, int y, int button)
	{
		super.mouseReleased(x, y, button);
		
		scrolling = false;
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException
	{
		if(keyCode == 1)
		{
			parent.setExpanding(false);
			mc.displayGuiScreen(parent);
		}
	}
	
	@Override
	public void updateScreen()
	{
		// scroll
		scroll += Mouse.getDWheel() / 10;
		
		int maxScroll = -textHeight + height - 146;
		if(maxScroll > 0)
			maxScroll = 0;
		
		if(scroll > 0)
			scroll = 0;
		else if(scroll < maxScroll)
			scroll = maxScroll;
		
		if(maxScroll == 0)
			scrollKnobPosition = 0;
		else
			scrollKnobPosition =
				(int)((height - 131) * scroll / (float)maxScroll);
		scrollKnobPosition += 2;
		
		// area
		Rectangle area =
			new Rectangle((width / 2 - 154), 60, 308, (height - 103));
		
		// slider data
		ArrayList<BasicSlider> sliders = item.getSettings();
		for(int i = 0; i < sliders.size(); i++)
		{
			BasicSlider slider = sliders.get(i);
			float value =
				(float)((slider.getValue() - slider.getMinimumValue()) / (slider
					.getMaximumValue() - slider.getMinimumValue()));
			sliderDatas[i].x = area.x + (int)((area.width - 10) * value);
			sliderDatas[i].value = value;
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		// title bar
		drawCenteredString(Fonts.segoe22, item.getName(), width / 2, 32,
			0xffffff);
		
		// GL settings
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glDisable(GL_CULL_FACE);
		glDisable(GL_TEXTURE_2D);
		glShadeModel(GL_SMOOTH);
		
		// box & shadow
		Rectangle area =
			new Rectangle((width / 2 - 154), 60, 308, (height - 103));
		glColor4f(0.25F, 0.25F, 0.25F, 0.5F);
		glBegin(GL_QUADS);
		{
			glVertex2d(area.x, area.y);
			glVertex2d(area.x + area.width, area.y);
			glVertex2d(area.x + area.width, area.y + area.height);
			glVertex2d(area.x, area.y + area.height);
		}
		glEnd();
		RenderUtil.boxShadow(area.x, area.y, area.x + area.width, area.y
			+ area.height);
		
		// scroll bar
		Rectangle scrollbar =
			new Rectangle(width / 2 + 170, 60, 12, height - 103);
		glColor4f(0.25F, 0.25F, 0.25F, 0.5F);
		glBegin(GL_QUADS);
		{
			glVertex2d(scrollbar.x, scrollbar.y);
			glVertex2d(scrollbar.x + scrollbar.width, scrollbar.y);
			glVertex2d(scrollbar.x + scrollbar.width, scrollbar.y
				+ scrollbar.height);
			glVertex2d(scrollbar.x, scrollbar.y + scrollbar.height);
		}
		glEnd();
		RenderUtil.boxShadow(scrollbar.x, scrollbar.y, scrollbar.x
			+ scrollbar.width, scrollbar.y + scrollbar.height);
		
		// scroll knob
		scrollbar.x += 2;
		scrollbar.y += scrollKnobPosition;
		scrollbar.width = 8;
		scrollbar.height = 24;
		glColor4f(0.25F, 0.25F, 0.25F, 0.5F);
		glBegin(GL_QUADS);
		{
			glVertex2d(scrollbar.x, scrollbar.y);
			glVertex2d(scrollbar.x + scrollbar.width, scrollbar.y);
			glVertex2d(scrollbar.x + scrollbar.width, scrollbar.y
				+ scrollbar.height);
			glVertex2d(scrollbar.x, scrollbar.y + scrollbar.height);
		}
		glEnd();
		RenderUtil.boxShadow(scrollbar.x, scrollbar.y, scrollbar.x
			+ scrollbar.width, scrollbar.y + scrollbar.height);
		RenderUtil.downShadow(scrollbar.x + 1, scrollbar.y + 8, scrollbar.x
			+ scrollbar.width - 1, scrollbar.y + 9);
		RenderUtil.downShadow(scrollbar.x + 1, scrollbar.y + 12, scrollbar.x
			+ scrollbar.width - 1, scrollbar.y + 13);
		RenderUtil.downShadow(scrollbar.x + 1, scrollbar.y + 16, scrollbar.x
			+ scrollbar.width - 1, scrollbar.y + 17);
		
		// scissor box
		RenderUtil.scissorBox(area.x, area.y, area.x + area.width, area.y
			+ area.height);
		glEnable(GL_SCISSOR_TEST);
		
		// sliders
		for(int i = 0; i < sliderDatas.length; i++)
		{
			SliderData sliderData = sliderDatas[i];
			
			// rail
			int x1 = area.x + 2;
			int x2 = x1 + area.width - 4;
			int y1 = sliderData.y + scroll + 4;
			int y2 = y1 + 4;
			glColor4f(0.25F, 0.25F, 0.25F, 0.25F);
			glBegin(GL_QUADS);
			{
				glVertex2d(x1, y1);
				glVertex2d(x2, y1);
				glVertex2d(x2, y2);
				glVertex2d(x1, y2);
			}
			glEnd();
			RenderUtil.invertedBoxShadow(x1, y1, x2, y2);
			
			// knob
			x1 = sliderData.x + 1;
			x2 = x1 + 8;
			y1 -= 2;
			y2 += 2;
			glColor4f(sliderData.value, 1F - sliderData.value, 0F, 0.75F);
			glBegin(GL_QUADS);
			{
				glVertex2d(x1, y1);
				glVertex2d(x2, y1);
				glVertex2d(x2, y2);
				glVertex2d(x1, y2);
			}
			glEnd();
			RenderUtil.boxShadow(x1, y1, x2, y2);
		}
		
		// text
		drawString(Fonts.segoe15, text, area.x + 2, area.y + scroll, 0xffffff);
		
		// buttons
		for(int i = 0; i < buttonList.size(); ++i)
			((GuiButton)buttonList.get(i)).drawButton(mc, mouseX, mouseY);
		
		// scissor box
		glDisable(GL_SCISSOR_TEST);
		
		// GL resets
		glEnable(GL_CULL_FACE);
		glEnable(GL_TEXTURE_2D);
		glDisable(GL_BLEND);
	}
	
	private class SliderData
	{
		public int x;
		public int y;
		public float value;
		
		public SliderData(int x, int y, float value)
		{
			this.x = x;
			this.y = y;
			this.value = value;
		}
	}
}
