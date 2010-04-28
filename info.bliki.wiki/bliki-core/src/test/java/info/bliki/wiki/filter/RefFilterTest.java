package info.bliki.wiki.filter;

import info.bliki.wiki.model.Reference;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

public class RefFilterTest extends FilterTestSupport {
  public RefFilterTest(String name) {
    super(name);
  }

  public static Test suite() {
    return new TestSuite(RefFilterTest.class);
  }

  public void testRef01() {
    assertEquals(
        "\n"
            + "<p>A <sup id=\"_ref-1\" class=\"reference\"><a href=\"#_note-1\" title=\"\">[1]</a></sup> Test</p><ol class=\"references\">\n"
            + "<li id=\"_note-1\"><b><a href=\"#_ref-1\" title=\"\">&#8593;</a></b> Reference</li></ol>",
        wikiModel.render("A <ref>Reference</ref> Test\n\n<references/>"));
    List<Reference> list = wikiModel.getReferences();
    Reference ref = list.get(0);
    assertTrue(ref.getRefString().equals("Reference"));
  }

  public void testRef02() {
    assertEquals(
        "\n"
            + "<p>A <sup id=\"_ref-1\" class=\"reference\"><a href=\"#_note-1\" title=\"\">[1]</a></sup> and a <sup id=\"_ref-2\" class=\"reference\"><a href=\"#_note-2\" title=\"\">[2]</a></sup> Test</p><ol class=\"references\">\n"
            + "<li id=\"_note-1\"><b><a href=\"#_ref-1\" title=\"\">&#8593;</a></b> first reference</li><li id=\"_note-2\"><b><a href=\"#_ref-2\" title=\"\">&#8593;</a></b> second reference</li></ol>",
        wikiModel
            .render("A <ref>first reference</ref> and a <ref>second reference</ref> Test\n\n<references/>"));
    List<Reference> list = wikiModel.getReferences();
    Reference ref = list.get(0);
    assertTrue(ref.getRefString().equals("first reference"));
    ref = list.get(1);
    assertTrue(ref.getRefString().equals("second reference"));
  }

  public void testRef03() {
    assertEquals(
        "\n"
            + "<p>aaa <sup id=\"_ref-Freitag_a\" class=\"reference\"><a href=\"#_note-Freitag\" title=\"\">[1]</a></sup> bbb<sup id=\"_ref-Arndt_a\" class=\"reference\"><a href=\"#_note-Arndt\" title=\"\">[2]</a></sup> <sup id=\"_ref-3\" class=\"reference\"><a href=\"#_note-3\" title=\"\">[3]</a></sup> </p><ol class=\"references\">\n"
            + "<li id=\"_note-1\"><b><a href=\"#_ref-1\" title=\"\">&#8593;</a></b> </li><li id=\"_note-2\"><b><a href=\"#_ref-2\" title=\"\">&#8593;</a></b> </li><li id=\"_note-3\"><b><a href=\"#_ref-3\" title=\"\">&#8593;</a></b> ccc</li></ol>",
        wikiModel
            .render("aaa <ref name=\"Freitag\"/> bbb<ref name=\"Arndt\"/> <ref>ccc</ref> <references/>"));
    List<Reference> list = wikiModel.getReferences();
    Reference ref = list.get(0);
    assertTrue(ref.getRefString().equals(""));
    ref = list.get(1);
    assertTrue(ref.getRefString().equals(""));
    ref = list.get(2);
    assertTrue(ref.getRefString().equals("ccc"));
  }

  public void testRef04() {
    assertEquals(
        "\n"
            + "<p>aaa <sup id=\"_ref-Freitag_a\" class=\"reference\"><a href=\"#_note-Freitag\" title=\"\">[1]</a></sup> bbb<sup id=\"_ref-Arndt_a\" class=\"reference\"><a href=\"#_note-Arndt\" title=\"\">[2]</a></sup> <sup id=\"_ref-3\" class=\"reference\"><a href=\"#_note-3\" title=\"\">[3]</a></sup> </p><ol class=\"references\">\n"
            + "<li id=\"_note-1\"><b><a href=\"#_ref-1\" title=\"\">&#8593;</a></b> </li><li id=\"_note-2\"><b><a href=\"#_ref-2\" title=\"\">&#8593;</a></b> arn</li><li id=\"_note-3\"><b><a href=\"#_ref-3\" title=\"\">&#8593;</a></b> ccc</li></ol>",
        wikiModel
            .render("aaa <ref name=Freitag></ref> bbb<ref	name=Arndt>arn</ref> <ref>ccc</ref> <references/>"));
    List<Reference> list = wikiModel.getReferences();
    Reference ref = list.get(0);
    assertTrue(ref.getRefString().equals(""));
    ref = list.get(1);
    assertTrue(ref.getRefString().equals("arn"));
    ref = list.get(2);
    assertTrue(ref.getRefString().equals("ccc"));
  }

  public void testRef05() {
    assertEquals(
        "\n"
            + "<p>aaa <sup id=\"_ref-Freitag_a\" class=\"reference\"><a href=\"#_note-Freitag\" title=\"\">[1]</a></sup> bbb<sup id=\"_ref-Arndt_a\" class=\"reference\"><a href=\"#_note-Arndt\" title=\"\">[2]</a></sup> <sup id=\"_ref-3\" class=\"reference\"><a href=\"#_note-3\" title=\"\">[3]</a></sup> </p><ol class=\"references\">\n"
            + "<li id=\"_note-1\"><b><a href=\"#_ref-1\" title=\"\">&#8593;</a></b> </li><li id=\"_note-2\"><b><a href=\"#_ref-2\" title=\"\">&#8593;</a></b> </li><li id=\"_note-3\"><b><a href=\"#_ref-3\" title=\"\">&#8593;</a></b> ccc</li></ol>",
        wikiModel
            .render("aaa <ref name=Freitag/> bbb<ref name=Arndt /> <ref>ccc</ref> <references/>"));
    List<Reference> list = wikiModel.getReferences();
    Reference ref = list.get(0);
    assertTrue(ref.getRefString().equals(""));
    ref = list.get(1);
    assertTrue(ref.getRefString().equals(""));
    ref = list.get(2);
    assertTrue(ref.getRefString().equals("ccc"));
  }

  public void testRef06() {
    assertEquals(
        "\n"
            + "<p>aaa <sup id=\"_ref-Freitag_a\" class=\"reference\"><a href=\"#_note-Freitag\" title=\"\">[1]</a></sup> bbb<sup id=\"_ref-A.26B_a\" class=\"reference\"><a href=\"#_note-A.26B\" title=\"\">[2]</a></sup> <sup id=\"_ref-3\" class=\"reference\"><a href=\"#_note-3\" title=\"\">[3]</a></sup> </p><ol class=\"references\">\n"
            + "<li id=\"_note-1\"><b><a href=\"#_ref-1\" title=\"\">&#8593;</a></b> </li><li id=\"_note-2\"><b><a href=\"#_ref-2\" title=\"\">&#8593;</a></b> </li><li id=\"_note-3\"><b><a href=\"#_ref-3\" title=\"\">&#8593;</a></b> ccc</li></ol>",
        wikiModel
            .render("aaa <ref name=Freitag/> bbb<ref name=A&B /> <ref>ccc</ref> <references/>"));
    List<Reference> list = wikiModel.getReferences();
    Reference ref = list.get(0);
    assertTrue(ref.getRefString().equals(""));
    ref = list.get(1);
    assertTrue(ref.getRefString().equals(""));
    ref = list.get(2);
    assertTrue(ref.getRefString().equals("ccc"));
  }

//  public void testRef07() {
//    assertEquals(
//        "",
//        wikiModel
//            .render("<ref>{{cite book |last=Riggs |first=Thomas |title=Contemporary Theatre, Film, and Television |publisher=[[Gale Research]] |date=2002 |page=117 |isbn=0787651168}}</ref>\n<references/>"));
//  }
//
//  public void testRef08() {
//    assertEquals(
//        "",
//        wikiModel
//            .render("<ref>{{cite journal|author=Joseph Kahn|title= Anarchism, the Creed That Won’t Stay Dead; The Spread of World Capitalism Resurrects a Long-Dormant Movement |year=2000|journal=[[The New York Times]]|issue=5 August}}{{cite journal|author=Colin Moynihan |title=Book Fair Unites Anarchists. In Spirit, Anyway|year=2007|journal=New York Times|issue=16 April}}</ref>\n<references/>"));
//  }
  
}