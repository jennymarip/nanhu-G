package xiangshan.rocc

import chipsalliance.rocketchip.config.Parameters
import chisel3._
import chisel3.util._
import freechips.rocketchip.diplomacy.{IdRange, LazyModule, LazyModuleImp}
import freechips.rocketchip.tilelink._

class RoCCInstruction extends Bundle {
  val funct = Bits(7.W)
  val rs2 = Bits(5.W)
  val rs1 = Bits(5.W)
  val xd = Bool()
  val xs1 = Bool()
  val xs2 = Bool()
  val rd = Bits(5.W)
  val opcode = Bits(7.W)
}
// xlen -> 64
class RoCCCommand(implicit p: Parameters) extends Bundle {
  val inst = new RoCCInstruction
  val rs1 = Bits(64.W)
  val rs2 = Bits(64.W)
}
class RoCCResponse(implicit p: Parameters) extends Bundle {
  val rd = Bits(5.W)
  val data = Bits(64.W)
}

class RoCCCoreIO(val nRoCCCSRs: Int = 0)(implicit p: Parameters) extends Bundle {
  val cmd = Flipped(Decoupled(new RoCCCommand))
  val resp = Decoupled(new RoCCResponse)
  // val mem = new HellaCacheIO
  val busy = Output(Bool())
  val interrupt = Output(Bool())
  val exception = Input(Bool())
  // val csrs = Flipped(Vec(nRoCCCSRs, new CustomCSRIO))
}

class RoccCore()(implicit p: Parameters) extends LazyModule{
    val node = TLClientNode(Seq(TLMasterPortParameters.v1(Seq(TLClientParameters(
      name = "my-client",
      sourceId = IdRange(0, 4),
      requestFifo = true)))))
    lazy val module = new RoccCoreImp(this)
}
class RoccCoreImp(outer: RoccCore) extends LazyModuleImp(outer){
    // val io = IO(new RoCCCoreIO)
    val (bus, edge) = outer.node.out.head
    bus <> DontCare
}