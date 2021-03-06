/*  Copyright (c) 2006-2007, Vladimir Nikic
    All rights reserved.
	
    Redistribution and use of this software in source and binary forms, 
    with or without modification, are permitted provided that the following 
    conditions are met:
	
 * Redistributions of source code must retain the above
      copyright notice, this list of conditions and the
      following disclaimer.
	
 * Redistributions in binary form must reproduce the above
      copyright notice, this list of conditions and the
      following disclaimer in the documentation and/or other
      materials provided with the distribution.
	
 * The name of HtmlCleaner may not be used to endorse or promote 
      products derived from this software without specific prior
      written permission.

    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
    AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
    IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
    ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
    LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
    CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
    SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
    INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
    CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
    ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
    POSSIBILITY OF SUCH DAMAGE.
	
    You can contact Vladimir Nikic by sending e-mail to
    nikic_vladimir@yahoo.com. Please include the word "HtmlCleaner" in the
    subject line.
 */

package info.bliki.htmlcleaner;

/**
 * <p>
 * HTML tag end token.
 * </p>
 * 
 * Created by: Vladimir Nikic<br/> Date: November, 2006.
 */
public class EndTagToken extends TagToken {

	public EndTagToken() {
	}

	public EndTagToken(String name) {
		super(name.toLowerCase());
	}

	@Override
	public boolean addAttribute(String attName, String attValue, boolean checkXSS) {
		// do nothing - simply ignore attributes in closing tag
		return true;
	}

	@Override
	public boolean isAllowedAttribute(String attName) {
		return false;
	}
	
	@Override
	public void serialize(XmlSerializer xmlSerializer) {
		// do nothing - simply ignore serialization
	}

	@Override
	public Object clone()  {
		EndTagToken et = (EndTagToken) super.clone();
		return et;
	}

	@Override
	public String getParents() {
		return null;
	}
}