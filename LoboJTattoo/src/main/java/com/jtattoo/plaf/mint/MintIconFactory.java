/*
* Copyright (c) 2002 and later by MH Software-Entwicklung. All Rights Reserved.
*
* JTattoo is multiple licensed. If your are an open source developer you can use
* it under the terms and conditions of the GNU General Public License version 2.0
* or later as published by the Free Software Foundation.
*
* see: gpl-2.0.txt
*
* If you pay for a license you will become a registered user who could use the
* software under the terms and conditions of the GNU Lesser General Public License
* version 2.0 or later with classpath exception as published by the Free Software
* Foundation.
*
* see: lgpl-2.0.txt
* see: classpath-exception.txt
*
* Registered users could also use JTattoo under the terms and conditions of the
* Apache License, Version 2.0 as published by the Apache Software Foundation.
*
* see: APACHE-LICENSE-2.0.txt
 */
package com.jtattoo.plaf.mint;

import javax.swing.Icon;

import com.jtattoo.plaf.AbstractIconFactory;
import com.jtattoo.plaf.BaseIcons;

/**
 * <p>MintIconFactory class.</p>
 *
 * Author Michael Hagen
 *
 */
public final class MintIconFactory implements AbstractIconFactory {

	private static MintIconFactory instance = null;

	/**
	 * <p>Getter for the field instance.</p>
	 *
	 * @return a {@link com.jtattoo.plaf.mint.MintIconFactory} object.
	 */
	public static synchronized MintIconFactory getInstance() {
		if (instance == null) {
			instance = new MintIconFactory();
		}
		return instance;
	}

	private MintIconFactory() {
	}

	/** {@inheritDoc} */
	@Override
	public Icon getCheckBoxIcon() {
		return BaseIcons.getCheckBoxIcon();
	}

	/** {@inheritDoc} */
	@Override
	public Icon getCloseIcon() {
		return MintIcons.getCloseIcon();
	}

	/** {@inheritDoc} */
	@Override
	public Icon getComboBoxIcon() {
		return BaseIcons.getComboBoxIcon();
	}

	/** {@inheritDoc} */
	@Override
	public Icon getDownArrowIcon() {
		return MintIcons.getDownArrowIcon();
	}

	/** {@inheritDoc} */
	@Override
	public Icon getFileChooserDetailViewIcon() {
		return BaseIcons.getFileChooserDetailViewIcon();
	}

	/** {@inheritDoc} */
	@Override
	public Icon getFileChooserHomeFolderIcon() {
		return BaseIcons.getFileChooserHomeFolderIcon();
	}

	/** {@inheritDoc} */
	@Override
	public Icon getFileChooserListViewIcon() {
		return BaseIcons.getFileChooserListViewIcon();
	}

	/** {@inheritDoc} */
	@Override
	public Icon getFileChooserNewFolderIcon() {
		return BaseIcons.getFileChooserNewFolderIcon();
	}

	/** {@inheritDoc} */
	@Override
	public Icon getFileChooserUpFolderIcon() {
		return BaseIcons.getFileChooserUpFolderIcon();
	}

	/** {@inheritDoc} */
	@Override
	public Icon getFileViewComputerIcon() {
		return BaseIcons.getFileViewComputerIcon();
	}

	/** {@inheritDoc} */
	@Override
	public Icon getFileViewFloppyDriveIcon() {
		return BaseIcons.getFileViewFloppyDriveIcon();
	}

	/** {@inheritDoc} */
	@Override
	public Icon getFileViewHardDriveIcon() {
		return BaseIcons.getFileViewHardDriveIcon();
	}

	/** {@inheritDoc} */
	@Override
	public Icon getIconIcon() {
		return MintIcons.getIconIcon();
	}

	/** {@inheritDoc} */
	@Override
	public Icon getLeftArrowIcon() {
		return MintIcons.getLeftArrowIcon();
	}

	/** {@inheritDoc} */
	@Override
	public Icon getMaxIcon() {
		return MintIcons.getMaxIcon();
	}

	/** {@inheritDoc} */
	@Override
	public Icon getMenuArrowIcon() {
		return BaseIcons.getMenuArrowIcon();
	}

	/** {@inheritDoc} */
	@Override
	public Icon getMenuCheckBoxIcon() {
		return BaseIcons.getMenuCheckBoxIcon();
	}

	/** {@inheritDoc} */
	@Override
	public Icon getMenuIcon() {
		return BaseIcons.getMenuIcon();
	}

	/** {@inheritDoc} */
	@Override
	public Icon getMenuRadioButtonIcon() {
		return BaseIcons.getMenuRadioButtonIcon();
	}

	/** {@inheritDoc} */
	@Override
	public Icon getMinIcon() {
		return MintIcons.getMinIcon();
	}

	/** {@inheritDoc} */
	@Override
	public Icon getOptionPaneErrorIcon() {
		return BaseIcons.getOptionPaneErrorIcon();
	}

	/** {@inheritDoc} */
	@Override
	public Icon getOptionPaneInformationIcon() {
		return BaseIcons.getOptionPaneInformationIcon();
	}

	/** {@inheritDoc} */
	@Override
	public Icon getOptionPaneQuestionIcon() {
		return BaseIcons.getOptionPaneQuestionIcon();
	}

	/** {@inheritDoc} */
	@Override
	public Icon getOptionPaneWarningIcon() {
		return BaseIcons.getOptionPaneWarningIcon();
	}

	/** {@inheritDoc} */
	@Override
	public Icon getPaletteCloseIcon() {
		return BaseIcons.getPaletteCloseIcon();
	}

	/** {@inheritDoc} */
	@Override
	public Icon getRadioButtonIcon() {
		return BaseIcons.getRadioButtonIcon();
	}

	/** {@inheritDoc} */
	@Override
	public Icon getRightArrowIcon() {
		return MintIcons.getRightArrowIcon();
	}

	/** {@inheritDoc} */
	@Override
	public Icon getSplitterDownArrowIcon() {
		return BaseIcons.getSplitterDownArrowIcon();
	}

	/** {@inheritDoc} */
	@Override
	public Icon getSplitterHorBumpIcon() {
		return BaseIcons.getSplitterHorBumpIcon();
	}

	/** {@inheritDoc} */
	@Override
	public Icon getSplitterLeftArrowIcon() {
		return BaseIcons.getSplitterLeftArrowIcon();
	}

	/** {@inheritDoc} */
	@Override
	public Icon getSplitterRightArrowIcon() {
		return BaseIcons.getSplitterRightArrowIcon();
	}

	/** {@inheritDoc} */
	@Override
	public Icon getSplitterUpArrowIcon() {
		return BaseIcons.getSplitterUpArrowIcon();
	}

	/** {@inheritDoc} */
	@Override
	public Icon getSplitterVerBumpIcon() {
		return BaseIcons.getSplitterVerBumpIcon();
	}

	/** {@inheritDoc} */
	@Override
	public Icon getThumbHorIcon() {
		return BaseIcons.getThumbHorIcon();
	}

	/** {@inheritDoc} */
	@Override
	public Icon getThumbHorIconRollover() {
		return BaseIcons.getThumbHorIconRollover();
	}

	/** {@inheritDoc} */
	@Override
	public Icon getThumbVerIcon() {
		return BaseIcons.getThumbVerIcon();
	}

	/** {@inheritDoc} */
	@Override
	public Icon getThumbVerIconRollover() {
		return BaseIcons.getThumbVerIconRollover();
	}

	/** {@inheritDoc} */
	@Override
	public Icon getTreeCloseIcon() {
		return BaseIcons.getTreeClosedIcon();
	}

	/** {@inheritDoc} */
	@Override
	public Icon getTreeCollapsedIcon() {
		return BaseIcons.getTreeCollapsedIcon();
	}

	/** {@inheritDoc} */
	@Override
	public Icon getTreeExpandedIcon() {
		return BaseIcons.getTreeExpandedIcon();
	}

	/** {@inheritDoc} */
	@Override
	public Icon getTreeLeafIcon() {
		return BaseIcons.getTreeLeafIcon();
	}

	/** {@inheritDoc} */
	@Override
	public Icon getTreeOpenIcon() {
		return BaseIcons.getTreeOpenedIcon();
	}

	/** {@inheritDoc} */
	@Override
	public Icon getUpArrowIcon() {
		return MintIcons.getUpArrowIcon();
	}

} // end of class MintIconFactory
