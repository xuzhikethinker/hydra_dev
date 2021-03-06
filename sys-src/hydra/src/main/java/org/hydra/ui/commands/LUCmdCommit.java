/**************************************************************************
 * Hydra: multi-headed version control system
 * (originally for the alpha-Flow project)
 * ==============================================
 * Copyright (C) 2009-2012 by 
 *   - Christoph P. Neumann (http://www.chr15t0ph.de)
 *   - Scott Hady
 **************************************************************************
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 **************************************************************************
 * $Id$
 *************************************************************************/
package org.hydra.ui.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hydra.core.Configuration;
import org.hydra.core.Stage;

/**
 * Commits a designated logical unit.
 *
 * @since 0.1
 * @version 0.2
 * @author Scott A. Hady
 */
public class LUCmdCommit extends CommandLogicalUnit {

	/** The Constant serialVersionUID. */
	public static final long serialVersionUID = 02L;

	/** The Constant DEFAULT_NAME. */
	public static final String DEFAULT_NAME = "LogicalUnit Commit";

	/** The Constant DEFAULT_ID. */
	public static final String DEFAULT_ID = "LUCmdCommit";

	/** The commit msg. */
	private String commitMsg;
	// Regular Expressions
	/** The cmd reg ex. */
	private final String cmdRegEx = "^\\s*(?i:lucommit)\\b";

	/** The lu reg ex. */
	private final String luRegEx = "((\\s+)(\\S+.*)\\b)?\\s+-m\\b";

	/** The msg reg ex. */
	private final String msgRegEx = "\\s+(.*)\\s*$";

	/** The cmd pattern. */
	private final Pattern cmdPattern = Pattern.compile(this.cmdRegEx);

	/** The complete pattern. */
	private final Pattern completePattern = Pattern.compile(this.cmdRegEx
			+ this.luRegEx + this.msgRegEx);

	/** The GROU p_ logicalunit. */
	private final int GROUP_LOGICALUNIT = 3;

	/** The GROU p_ message. */
	private final int GROUP_MESSAGE = 4;

	/**
	 * Specialized Constructor which designates which stage use to find the
	 * logical unit's to commit.
	 *
	 * @param stage
	 *            Stage.
	 */
	public LUCmdCommit(final Stage stage) {
		super(LUCmdCommit.DEFAULT_NAME, LUCmdCommit.DEFAULT_ID, stage);
	}

	/**
	 * Specialized constructor that designates the stage, logical unit's name
	 * and the commit message to use for the commit.
	 *
	 * @param stage
	 *            Stage.
	 * @param luName
	 *            String.
	 * @param commitMsg
	 *            String.
	 */
	public LUCmdCommit(final Stage stage, final String luName,
			final String commitMsg) {
		super(LUCmdCommit.DEFAULT_NAME, LUCmdCommit.DEFAULT_ID, stage, luName);
		this.commitMsg = commitMsg;
	}

	/**
	 * COMMAND METHODS OVERRIDDEN *********************************************.
	 * 
	 * @return the command pattern
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Factory Method - Command Pattern accepts 'lucommit' as the command.
	 */
	@Override
	public Pattern getCommandPattern() {
		return this.cmdPattern;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Factory Method - Complete Pattern accepts 'lucommit {[luname]} -m
	 * [message]'.
	 */
	@Override
	public Pattern getCompletePattern() {
		return this.completePattern;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Template Method - Process the matcher to extract the logical unit and
	 * message parameters.
	 */
	@Override
	public boolean processMatcher(final Matcher matcher) {
		this.commitMsg = matcher.group(this.GROUP_MESSAGE);
		return this.processLogicalUnitName(matcher
				.group(this.GROUP_LOGICALUNIT));
	}

	/**
	 * {@inheritDoc}
	 *
	 * Commit the designated logical unit.
	 */
	@Override
	public boolean execute() {
		boolean success = true;
		String exMsg = "FAILURE: Commit of Logical Unit [" + this.luName
				+ "] Unsuccessful.\n";
		try {
			final String commitHash = this.stage.getLogicalUnit(this.luName)
					.commitValidPath(Configuration.getInstance().getUserId(),
							this.commitMsg);
			exMsg = "Logical Unit [" + this.luName
					+ "] Successfully Committed. Hash [" + commitHash + "].\n";
		} catch (final Exception e) {
			this.logger.exception("Unable to Commit Logical Unit.", e);
			success = false;
		}
		this.writer.println(exMsg, this.cmdVerbosity);
		return success;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Return a string describing the commands usage.
	 */
	@Override
	public String getUsage() {
		return "luCommit {<luName>} -m <message>\tCommit Logical Unit.";
	}

}
