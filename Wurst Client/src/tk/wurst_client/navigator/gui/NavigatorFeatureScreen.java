package tk.wurst_client.navigator.gui;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import org.darkstorm.minecraft.gui.component.basic.BasicSlider;
import org.darkstorm.minecraft.gui.util.RenderUtil;
import org.lwjgl.input.Mouse;

import tk.wurst_client.WurstClient;
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
	private ButtonData activeButton;
	private GuiButton primaryButton;
	private int scrollKnobPosition = 2;
	private boolean scrolling;
	private int sliding = -1;
	private int textHeight;
	private String text;
	private ArrayList<ButtonData> buttonDatas = new ArrayList<>();
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
		buttonList.clear();
		buttonDatas.clear();
		
		// primary button
		String primaryAction = item.getPrimaryAction();
		boolean hasPrimaryAction = !primaryAction.isEmpty();
		boolean hasTutorial = !item.getTutorialPage().isEmpty();
		if(hasPrimaryAction)
		{
			primaryButton =
				new GuiButton(0, width / 2 - 151, height - 65, hasTutorial
					? 149 : 302, 18, primaryAction);
			buttonList.add(primaryButton);
		}
		
		// tutorial button
		if(hasTutorial)
			buttonList.add(new GuiButton(1, width / 2
				+ (hasPrimaryAction ? 2 : -151), height - 65, hasPrimaryAction
				? 149 : 302, 20, "Tutorial"));
		
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
			text += "\n\nSettings:";
			sliderDatas = new SliderData[sliders.size()];
			for(int i = 0; i < sliders.size(); i++)
			{
				BasicSlider slider = sliders.get(i);
				
				// text
				text += "\n" + slider.getText() + ":\n";
				
				// value
				String value;
				switch(slider.getValueDisplay())
				{
					case DECIMAL:
						value = Double.toString(slider.getValue());
						break;
					case DEGREES:
						value = (int)slider.getValue() + "°";
						break;
					case INTEGER:
						value = Integer.toString((int)slider.getValue());
						break;
					case PERCENTAGE:
						value = (slider.getValue() * 100D) + "%";
						break;
					default:
					case NONE:
						value = "";
						break;
				}
				
				// percentage
				float percentage =
					(float)((slider.getValue() - slider.getMinimumValue()) / (slider
						.getMaximumValue() - slider.getMinimumValue()));
				
				// x
				int x = area.x + (int)((area.width - 10) * percentage);
				
				// y
				int y = area.y + Fonts.segoe15.getStringHeight(text);
				
				sliderDatas[i] = new SliderData(x, y, percentage, value);
			}
		}
		
		// keybinds
		HashMap<String, String> possibleKeybinds = item.getPossibleKeybinds();
		if(!possibleKeybinds.isEmpty())
		{
			// heading
			text += "\n\nKeybinds:";
			
			// add keybind button
			ButtonData addKeybindButton =
				new ButtonData(area.x + area.width - 16, area.y
					+ Fonts.segoe15.getStringHeight(text) - 8, 12, 8, "+")
				{
					@Override
					public void press()
					{
						// add keybind
					}
				};
			buttonDatas.add(addKeybindButton);
			
			// keybind list
			boolean noKeybindsSet = true;
			for(Entry<String, String> entry : WurstClient.INSTANCE.keybinds
				.entrySet())
			{
				String keybindDescription =
					possibleKeybinds.get(entry.getValue());
				if(keybindDescription != null)
				{
					if(noKeybindsSet)
						noKeybindsSet = false;
					text += "\n" + entry.getKey() + ": " + keybindDescription;
				}
			}
			if(noKeybindsSet)
				text += "\nNone";
			else
			{
				// remove keybind button
				buttonDatas.add(new ButtonData(addKeybindButton.x,
					addKeybindButton.y, addKeybindButton.width,
					addKeybindButton.height, "-")
				{
					@Override
					public void press()
					{
						// remove keybind
					}
				});
				addKeybindButton.x -= 16;
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
		
		// scrollbar
		if(new Rectangle(width / 2 + 170, 60, 12, height - 103).contains(x, y))
		{
			scrolling = true;
			return;
		}
		
		// buttons
		if(activeButton != null)
		{
			mc.getSoundHandler().playSound(
				PositionedSoundRecord.createPositionedSoundRecord(
					new ResourceLocation("gui.button.press"), 1.0F));
			activeButton.press();
			return;
		}
		
		// sliders
		Rectangle area =
			new Rectangle((width / 2 - 154), 60, 308, (height - 103));
		if(area.contains(x, y))
		{
			area.height = 12;
			for(int i = 0; i < sliderDatas.length; i++)
			{
				SliderData sliderData = sliderDatas[i];
				area.y = sliderData.y + scroll;
				if(area.contains(x, y))
				{
					sliding = i;
					return;
				}
			}
		}
	}
	
	@Override
	protected void mouseClickMove(int mouseX, int mouseY,
		int clickedMouseButton, long timeSinceLastClick)
	{
		if(clickedMouseButton != 0)
			return;
		if(scrolling)
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
		}else if(sliding != -1)
		{
			BasicSlider slider = item.getSettings().get(sliding);
			float percentage = (mouseX - (width / 2 - 154)) / 298F;
			
			if(percentage > 1F)
				percentage = 1F;
			else if(percentage < 0F)
				percentage = 0F;
			
			slider.setValue((long)((slider.getMaximumValue() - slider
				.getMinimumValue()) * percentage / slider.getIncrement())
				* 1e6 * slider.getIncrement() / 1e6 + slider.getMinimumValue());
		}
	}
	
	@Override
	public void mouseReleased(int x, int y, int button)
	{
		super.mouseReleased(x, y, button);
		
		scrolling = false;
		sliding = -1;
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
			SliderData sliderData = sliderDatas[i];
			
			// value
			String value;
			switch(slider.getValueDisplay())
			{
				case DECIMAL:
					value = Double.toString(slider.getValue());
					break;
				case DEGREES:
					value = (int)slider.getValue() + "°";
					break;
				case INTEGER:
					value = Integer.toString((int)slider.getValue());
					break;
				case PERCENTAGE:
					value = (slider.getValue() * 100D) + "%";
					break;
				default:
				case NONE:
					value = "";
					break;
			}
			sliderData.value = value;
			
			// percentage
			sliderData.percentage =
				(float)((slider.getValue() - slider.getMinimumValue()) / (slider
					.getMaximumValue() - slider.getMinimumValue()));
			
			// x
			sliderData.x =
				area.x + (int)((area.width - 10) * sliderData.percentage);
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
			+ area.height - (buttonList.isEmpty() ? 0 : 24));
		glEnable(GL_SCISSOR_TEST);
		
		// sliders
		for(SliderData sliderData : sliderDatas)
		{
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
			glColor4f(sliderData.percentage, 1F - sliderData.percentage, 0F,
				0.75F);
			glBegin(GL_QUADS);
			{
				glVertex2d(x1, y1);
				glVertex2d(x2, y1);
				glVertex2d(x2, y2);
				glVertex2d(x1, y2);
			}
			glEnd();
			RenderUtil.boxShadow(x1, y1, x2, y2);
			
			// value
			x1 =
				area.x + area.width
					- Fonts.segoe15.getStringWidth(sliderData.value) - 2;
			y1 -= 12;
			drawString(Fonts.segoe15, sliderData.value, x1, y1, 0xffffff);
			glDisable(GL_TEXTURE_2D);
		}
		
		// buttons
		activeButton = null;
		for(ButtonData buttonData : buttonDatas)
		{
			Rectangle buttonArea = new Rectangle(buttonData);
			buttonArea.y += scroll;
			int x2 = buttonArea.x + buttonArea.width;
			int y2 = buttonArea.y + buttonArea.height;
			
			if(buttonArea.contains(mouseX, mouseY))
			{
				activeButton = buttonData;
				glColor4f(0.375F, 0.375F, 0.375F, 0.25F);
			}else
				glColor4f(0.25F, 0.25F, 0.25F, 0.25F);
			glBegin(GL_QUADS);
			{
				glVertex2d(buttonArea.x, buttonArea.y);
				glVertex2d(x2, buttonArea.y);
				glVertex2d(x2, y2);
				glVertex2d(buttonArea.x, y2);
			}
			glEnd();
			RenderUtil.boxShadow(buttonArea.x, buttonArea.y, x2, y2);
			
			drawCenteredString(Fonts.segoe18, buttonData.displayString,
				buttonArea.x + buttonData.width / 2 - 1, buttonArea.y
					+ (buttonData.height - 12) / 2 - 1, 0xffffff);
			glDisable(GL_TEXTURE_2D);
		}
		
		// text
		drawString(Fonts.segoe15, text, area.x + 2, area.y + scroll, 0xffffff);
		
		// scissor box
		glDisable(GL_SCISSOR_TEST);
		
		// buttons below scissor box
		if(!buttonList.isEmpty())
		{
			for(int i = 0; i < buttonList.size(); i++)
			{
				GuiButton button = (GuiButton)buttonList.get(i);
				Rectangle buttonArea =
					new Rectangle(button.xPosition, button.yPosition,
						button.getButtonWidth(), 18);
				
				if(buttonArea.contains(mouseX, mouseY))
					glColor4f(0.375F, 0.375F, 0.375F, 0.25F);
				else
					glColor4f(0.25F, 0.25F, 0.25F, 0.25F);
				
				int x2 = buttonArea.x + buttonArea.width;
				int y2 = buttonArea.y + buttonArea.height;
				
				glDisable(GL_TEXTURE_2D);
				glBegin(GL_QUADS);
				{
					glVertex2d(buttonArea.x, buttonArea.y);
					glVertex2d(x2, buttonArea.y);
					glVertex2d(x2, y2);
					glVertex2d(buttonArea.x, y2);
				}
				glEnd();
				RenderUtil.boxShadow(buttonArea.x, buttonArea.y, x2, y2);
				
				drawCenteredString(Fonts.segoe18, button.displayString,
					buttonArea.x + buttonArea.width / 2, buttonArea.y + 2,
					0xffffff);
			}
		}
		
		// GL resets
		glEnable(GL_CULL_FACE);
		glEnable(GL_TEXTURE_2D);
		glDisable(GL_BLEND);
	}
	
	private class SliderData
	{
		public int x;
		public int y;
		public float percentage;
		public String value;
		
		public SliderData(int x, int y, float percentage, String value)
		{
			this.x = x;
			this.y = y;
			this.percentage = percentage;
			this.value = value;
		}
	}
	
	private abstract class ButtonData extends Rectangle
	{
		public String displayString = "";
		
		public ButtonData(int x, int y, int width, int height,
			String displayString)
		{
			super(x, y, width, height);
			this.displayString = displayString;
		}
		
		public abstract void press();
	}
}
