package scratchobfuscator.modules;

import static org.hamcrest.CoreMatchers.instanceOf;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import scratchlib.objects.ScratchObject;
import scratchlib.objects.fixed.collections.ScratchObjectAbstractDictionary;
import scratchlib.objects.fixed.data.ScratchObjectAbstractString;
import scratchlib.objects.fixed.data.ScratchObjectSymbol;
import scratchlib.objects.fixed.data.ScratchObjectUtf8;
import scratchlib.objects.fixed.forms.ScratchObjectColorForm;
import scratchlib.objects.inline.ScratchObjectSmallInteger;
import scratchlib.objects.inline.ScratchObjectSmallInteger16;
import scratchlib.project.ScratchProject;
import scratchlib.project.ScratchVersion;


public class CleanInfoModuleTest
{
    @Test
    public void removesThumbnail()
    {
        CleanInfoModule module = new CleanInfoModule();

        ScratchProject project = new ScratchProject(ScratchVersion.SCRATCH14);
        project.setInfoProperty(ScratchProject.INFO_THUMBNAIL, new ScratchObjectColorForm());

        assertThat(project.getInfoProperty(ScratchProject.INFO_THUMBNAIL), instanceOf(ScratchObjectColorForm.class));

        module.process(project);
        assertNull(project.getInfoProperty(ScratchProject.INFO_THUMBNAIL));
    }

    @Test
    public void clearsAuthor()
    {
        CleanInfoModule module = new CleanInfoModule();

        ScratchProject project = new ScratchProject(ScratchVersion.SCRATCH14);
        project.setInfoProperty(ScratchProject.INFO_AUTHOR, new ScratchObjectUtf8("author"));

        assertEquals("author",
                ((ScratchObjectAbstractString) project.getInfoProperty(ScratchProject.INFO_AUTHOR)).getValue());

        module.process(project);
        assertEquals("",
                ((ScratchObjectAbstractString) project.getInfoProperty(ScratchProject.INFO_AUTHOR)).getValue());
    }

    @Test
    public void clearsComment()
    {
        CleanInfoModule module = new CleanInfoModule();

        ScratchProject project = new ScratchProject(ScratchVersion.SCRATCH14);
        project.setInfoProperty(ScratchProject.INFO_COMMENT, new ScratchObjectUtf8("comment"));

        assertEquals("comment",
                ((ScratchObjectAbstractString) project.getInfoProperty(ScratchProject.INFO_COMMENT)).getValue());

        module.process(project);
        assertEquals("",
                ((ScratchObjectAbstractString) project.getInfoProperty(ScratchProject.INFO_COMMENT)).getValue());
    }

    @Test
    public void clearsHistory()
    {
        CleanInfoModule module = new CleanInfoModule();

        ScratchProject project = new ScratchProject(ScratchVersion.SCRATCH14);
        project.setInfoProperty(ScratchProject.INFO_HISTORY, new ScratchObjectUtf8("history\r"));

        assertEquals("history\r",
                ((ScratchObjectAbstractString) project.getInfoProperty(ScratchProject.INFO_HISTORY)).getValue());

        module.process(project);
        assertEquals("\r",
                ((ScratchObjectAbstractString) project.getInfoProperty(ScratchProject.INFO_HISTORY)).getValue());
    }

    @Test
    public void keepsOtherProperties()
    {
        CleanInfoModule module = new CleanInfoModule();

        ScratchProject project = new ScratchProject(ScratchVersion.SCRATCH14);
        project.setInfoProperty(ScratchProject.INFO_PLATFORM, new ScratchObjectSymbol("scratchOS"));
        project.setInfoProperty("unknownProp", new ScratchObjectSmallInteger16((short) 42));

        ScratchObjectSmallInteger intKey = new ScratchObjectSmallInteger(1000);
        ((ScratchObjectAbstractDictionary) project.getInfoSection().get()).put(intKey,
                new ScratchObjectUtf8("property with non-string key"));

        module.process(project);

        assertEquals("scratchOS",
                ((ScratchObjectAbstractString) project.getInfoProperty(ScratchProject.INFO_PLATFORM)).getValue());
        assertEquals(42, ((ScratchObjectSmallInteger16) project.getInfoProperty("unknownProp")).intValue());

        ScratchObject intVal = ((ScratchObjectAbstractDictionary) project.getInfoSection().get()).get(intKey);
        assertEquals("property with non-string key", ((ScratchObjectUtf8) intVal).getValue());
    }
}
