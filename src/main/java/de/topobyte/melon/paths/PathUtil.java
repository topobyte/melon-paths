// Copyright 2017 Sebastian Kuerten
//
// This file is part of melon-paths.
//
// melon-paths is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// melon-paths is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with melon-paths. If not, see <http://www.gnu.org/licenses/>.

package de.topobyte.melon.paths;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathUtil
{

	final static Logger logger = LoggerFactory.getLogger(PathUtil.class);

	public static List<Path> list(Path directory) throws IOException
	{
		try (DirectoryStream<Path> stream = Files
				.newDirectoryStream(directory)) {
			return newArrayList(stream);
		}
	}

	public static List<Path> find(Path directory, String glob)
			throws IOException
	{
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory,
				glob)) {
			return newArrayList(stream);
		}
	}

	public static enum AccessDeniedLogOption {
		LOG_WARN,
		LOG_DEBUG,
		LOG_INFO
	}

	public static enum AccessDeniedActionOption {
		SKIP,
		TERMINATE,
		FAIL
	}

	public static List<Path> findRecursive(Path pathData, final String glob)
			throws IOException
	{
		return findRecursive(pathData, glob, Integer.MAX_VALUE, false);
	}

	public static List<Path> findRecursive(Path pathData, final String glob,
			boolean followLinks) throws IOException
	{
		return findRecursive(pathData, glob, Integer.MAX_VALUE, followLinks);
	}

	public static List<Path> findRecursive(Path pathData, final String glob,
			int maxDepth, boolean followLinks) throws IOException
	{
		return findRecursive(pathData, glob, AccessDeniedActionOption.SKIP,
				AccessDeniedLogOption.LOG_DEBUG, maxDepth, followLinks);
	}

	public static List<Path> findRecursive(Path pathData, final String glob,
			final AccessDeniedActionOption accessDeniedActionOption,
			final AccessDeniedLogOption accessDeniedLogOption, int maxDepth,
			boolean followLinks) throws IOException
	{
		final List<Path> results = new ArrayList<>();

		EnumSet<FileVisitOption> options = EnumSet
				.noneOf(FileVisitOption.class);
		if (followLinks) {
			options.add(FileVisitOption.FOLLOW_LINKS);
		}

		Files.walkFileTree(pathData, options, maxDepth,
				new SimpleFileVisitor<Path>() {

					@Override
					public FileVisitResult preVisitDirectory(Path dir,
							BasicFileAttributes attrs) throws IOException
					{
						try (DirectoryStream<Path> stream = Files
								.newDirectoryStream(dir, glob)) {
							addAll(results, stream);
						}
						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult visitFileFailed(Path file,
							IOException io) throws IOException
					{
						if (io instanceof AccessDeniedException) {
							switch (accessDeniedLogOption) {
							case LOG_WARN:
								logger.warn(
										"access denied: " + io.getMessage());
								break;
							case LOG_DEBUG:
								logger.debug(
										"access denied: " + io.getMessage());
								break;
							case LOG_INFO:
								logger.info(
										"access denied: " + io.getMessage());
								break;
							}
							switch (accessDeniedActionOption) {
							case SKIP:
								return FileVisitResult.SKIP_SUBTREE;
							case TERMINATE:
								return FileVisitResult.TERMINATE;
							case FAIL:
								throw io;
							}
						} else {
							logger.warn(io.getMessage(), io);
						}
						return super.visitFileFailed(file, io);
					}

				});
		return results;
	}

	public static String getBasename(String name)
	{
		int index = name.lastIndexOf(".");
		if (index < 0) {
			return name;
		}
		return name.substring(0, index);
	}

	public static String getBasename(Path file)
	{
		return getBasename(file.getFileName().toString());
	}

	private static final Path ROOT = Paths.get("/");

	public static Path relative(Path path)
	{
		if (!path.isAbsolute()) {
			return path;
		}
		return ROOT.relativize(path);
	}

	public static void createParentDirectories(Path path) throws IOException
	{
		Path parent = path.getParent();
		if (parent == null) {
			return;
		}
		Files.createDirectories(parent);
	}

	private static <T> List<T> newArrayList(Iterable<T> iterable)
	{
		List<T> list = new ArrayList<>();
		for (T path : iterable) {
			list.add(path);
		}
		return list;
	}

	private static <T> void addAll(List<T> list, Iterable<T> iterable)
	{
		for (T value : iterable) {
			list.add(value);
		}
	}

}
