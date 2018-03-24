/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.azkfw.munchkin.ui;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author Kawakicchi
 *
 */
public class IgnoreUndoableEdit implements UndoableEdit {

	private UndoableEdit edit;

	public IgnoreUndoableEdit(final UndoableEdit edit) {
		this.edit = edit;
	}

	@Override
	public void undo() throws CannotUndoException {
		edit.undo();
	}

	@Override
	public boolean canUndo() {
		boolean result = edit.canUndo();
		return result;
	}

	@Override
	public void redo() throws CannotRedoException {
		edit.redo();
	}

	@Override
	public boolean canRedo() {
		boolean result = edit.canRedo();
		return result;
	}

	@Override
	public void die() {
		edit.die();
	}

	@Override
	public boolean addEdit(final UndoableEdit anEdit) {
		boolean result = edit.addEdit(anEdit);
		return result;
	}

	@Override
	public boolean replaceEdit(final UndoableEdit anEdit) {
		boolean result = edit.replaceEdit(anEdit);
		return result;
	}

	@Override
	public boolean isSignificant() {
		return false;
	}

	@Override
	public String getPresentationName() {
		String result = edit.getPresentationName();
		return result;
	}

	@Override
	public String getUndoPresentationName() {
		String result = edit.getUndoPresentationName();
		return result;
	}

	@Override
	public String getRedoPresentationName() {
		String result = edit.getRedoPresentationName();
		return result;
	}
}