///**
// * Copyright (c) 2011-2013 Armin Töpfer
// *
// * This file is part of InDelFixer.
// *
// * InDelFixer is free software: you can redistribute it and/or modify it under
// * the terms of the GNU General Public License as published by the Free Software
// * Foundation, either version 3 of the License, or any later version.
// *
// * InDelFixer is distributed in the hope that it will be useful, but WITHOUT ANY
// * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
// * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License along with
// * InDelFixer. If not, see <http://www.gnu.org/licenses/>.
// */
//package ch.ethz.bsse.indelfixer.refinement;
//
//import ch.ethz.bsse.indelfixer.stored.Globals;
//import ch.ethz.bsse.indelfixer.utils.Utils;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.concurrent.Callable;
//import net.sf.samtools.AlignmentBlock;
//import net.sf.samtools.CigarElement;
//import static net.sf.samtools.CigarOperator.D;
//import static net.sf.samtools.CigarOperator.EQ;
//import static net.sf.samtools.CigarOperator.H;
//import static net.sf.samtools.CigarOperator.I;
//import static net.sf.samtools.CigarOperator.M;
//import static net.sf.samtools.CigarOperator.N;
//import static net.sf.samtools.CigarOperator.P;
//import static net.sf.samtools.CigarOperator.S;
//import static net.sf.samtools.CigarOperator.X;
//import net.sf.samtools.SAMRecord;
//
///**
// * @author Armin Töpfer (armin.toepfer [at] gmail.com)
// */
//public class SFRComputing implements Callable<ReadTMP> {
//
//    final SAMRecord samRecord;
//
//    public SFRComputing(final SAMRecord samRecord) {
//        this.samRecord = samRecord;
//    }
//
//    @Override
//    public ReadTMP call() {
//        try {
//            List<AlignmentBlock> alignmentBlocks = samRecord.getAlignmentBlocks();
//            if (alignmentBlocks.isEmpty()) {
//                return null;
//            }
//            int refStart = alignmentBlocks.get(0).getReferenceStart() + alignmentBlocks.get(0).getReadStart() - 1;
//            int readStart = 0;
//            List<Byte> buildRead = new ArrayList<>();
//            List<Double> buildQuality = new ArrayList<>();
//            boolean hasQuality;
//            for (CigarElement c : samRecord.getCigar().getCigarElements()) {
//                switch (c.getOperator()) {
//                    case X:
//                    case EQ:
//                    case M:
//                        if ((readStart + c.getLength()) > samRecord.getReadBases().length) {
//                            System.out.println("");
//                            System.out.println("C:" + c.getOperator());
//                            System.out.println("L:" + c.getLength());
//                            System.out.println("N:" + samRecord.getReadBases().length);
//                            System.out.println("R:" + readStart);
//                            System.out.println("S:" + (alignmentBlocks.get(0).getReadStart() - 1));
//                            System.out.println("T:" + samRecord.getCigar().toString());
//                            System.exit(9);
//                        }
//                        for (int i = 0; i < c.getLength(); i++) {
//                            byte b = samRecord.getReadBases()[readStart];
//                            buildRead.add(b);
//                            readStart++;
//                        }
//                        break;
//                    case I:
//                        readStart += c.getLength();
//                        break;
//                    case D:
//                        for (int i = 0; i < c.getLength(); i++) {
//                            buildRead.add((byte) "-".charAt(0));
//                        }
//                        break;
//                    case S:
//                        readStart += c.getLength();
//                        break;
//                    case H:
//                        break;
//                    case P:
//                        System.out.println("P");
//                        System.exit(9);
//                        break;
//                    case N:
//                        System.out.println("N");
//                        System.exit(9);
//                        break;
//                    default:
//                        break;
//                }
//            }
//            double[] quality = new double[buildQuality.size()];
//            //---
//            //cut read
//            byte[] readBases = Utils.convertRead(buildRead.toArray(new Byte[buildRead.size()]));
//            int readEnd = refStart + readBases.length;
//            if (Globals.getINSTANCE().isWINDOW()) {
//
//                int from = Globals.getINSTANCE().getWINDOW_BEGIN();
//                int to = Globals.getINSTANCE().getWINDOW_END();
//                int length = readBases.length;
//                try {
//                    if (refStart > to || readEnd < from) {
//                        return null;
//                    }
//                    //leftover
//                    if (refStart < from && readEnd <= to) {
//                        readBases = Arrays.copyOfRange(readBases, from - refStart, length);
//                        if (hasQuality) {
//                            quality = Arrays.copyOfRange(quality, from - refStart, length);
//                        }
//                        refStart = from;
//                    } else if (refStart >= from && readEnd > to) {
//                        //rightover
//                        readBases = Arrays.copyOfRange(readBases, 0, to - refStart);
//                        if (hasQuality) {
//                            quality = Arrays.copyOfRange(quality, 0, to - refStart);
//                        }
//                    } else if (refStart >= from && readEnd <= to) {
//                        //inner
//                    } else if (refStart < from && readEnd > to) {
//                        //outer
//                        readBases = Arrays.copyOfRange(readBases, from - refStart, to - refStart - (from - refStart));
//                        if (hasQuality) {
//                            quality = Arrays.copyOfRange(quality, from - refStart, to - refStart - (from - refStart));
//                        }
//                    } else {
//                        System.err.println("");
//                        System.err.println("start: " + refStart + "\t+end:" + readEnd);
//                        System.err.println("w00t");
//                    }
//                } catch (Exception e) {
//                    System.err.println("");
//                    System.err.println("start: " + refStart + "\t+end:" + readEnd);
//                    System.err.println("w00t");
//                }
//            }
//
//            //---
//
//            String name = samRecord.getReadName();
//
//            return new ReadTMP(name, quality, readBases, refStart, hasQuality);
//
//        } catch (ArrayIndexOutOfBoundsException e) {
//            System.err.println();
//            System.err.println(e);
//            System.err.println();
//        } catch (Exception e) {
//            System.err.println("WOOT:" + e);
//            // Sometimes CIGAR is not correct. In that case we simply ignore it/
//        }
//        return null;
//    }
//}
