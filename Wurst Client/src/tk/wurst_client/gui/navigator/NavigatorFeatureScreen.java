package tk.wurst_client.gui.navigator;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Rectangle;
import java.io.IOException;

import net.minecraft.client.gui.GuiScreen;

import org.darkstorm.minecraft.gui.util.RenderUtil;
import org.lwjgl.input.Mouse;

import tk.wurst_client.font.Fonts;
import tk.wurst_client.navigator.NavigatorItem;

public class NavigatorFeatureScreen extends GuiScreen
{
	private int scroll = 0;
	private NavigatorItem item;
	private NavigatorScreen parent;
	
	public NavigatorFeatureScreen(NavigatorItem item, NavigatorScreen parent)
	{
		this.item = item;
		this.parent = parent;
	}
	
	@Override
	public void initGui()
	{	
		
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
	}
	
	@Override
	public void mouseReleased(int x, int y, int button)
	{
		super.mouseReleased(x, y, button);
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
		scroll += Mouse.getDWheel() / 10;
		if(scroll > 0)
			scroll = 0;
		else
		{
			int maxScroll = 0;
			if(maxScroll > 0)
				maxScroll = 0;
			if(scroll < maxScroll)
				scroll = maxScroll;
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		// title bar
		Fonts.segoe22.drawString(item.getName(), width / 2 - 150, 32, 0xffffff);
		
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
		
		// GL resets
		glEnable(GL_CULL_FACE);
		glEnable(GL_TEXTURE_2D);
		glDisable(GL_BLEND);
	}
}
