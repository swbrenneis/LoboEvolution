/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.javascript.ast;

import org.mozilla.javascript.Token;

/**
 * Node representing comments.
 * Node type is {@link Token#COMMENT}.
 * @author utente
 * @version $Id: $Id
 */
public class Comment extends AstNode {

    private String value;
    private Token.CommentType commentType;

    {
        type = Token.COMMENT;
    }

    /**
     * Constructs a new Comment
     *
     * @param pos the start position
     * @param len the length including delimiter(s)
     * @param type the comment type
     * @param value the value of the comment, as a string
     */
    public Comment(int pos, int len, Token.CommentType type, String value) {
        super(pos, len);
        commentType = type;
        this.value = value;
    }

    /**
     * Returns the comment style
     *
     * @return a {@link org.mozilla.javascript.Token.CommentType} object.
     */
    public Token.CommentType getCommentType() {
        return commentType;
    }

    /**
     * Sets the comment style
     *
     * @param type the comment style, a
     * {@link org.mozilla.javascript.Token.CommentType}
     */
    public void setCommentType(Token.CommentType type) {
        this.commentType = type;
    }

    /**
     * Returns a string of the comment value.
     *
     * @return a {@link java.lang.String} object.
     */
    public String getValue() {
        return value;
    }

    /**
     * Set the comment Value with the new commentString. and updates the length with new Length.
     *
     * @param commentString a {@link java.lang.String} object.
     */
    public void setValue(String commentString) {
        this.value = commentString;
        this.setLength(this.value.length());
    }

    /** {@inheritDoc} */
    @Override
    public String toSource(int depth) {
        StringBuilder sb = new StringBuilder(getLength() + 10);
        sb.append(makeIndent(depth));
        sb.append(value);
        if(Token.CommentType.BLOCK_COMMENT == this.getCommentType()) {
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     *
     * Comment nodes are not visited during normal visitor traversals,
     * but comply with the {@link AstNode#visit} interface.
     */
    @Override
    public void visit(NodeVisitor v) {
        v.visit(this);
    }
}
